package com.github.gaboss44.ecolpr.core.transition

import com.github.gaboss44.ecolpr.api.event.transition.TransitionAttemptEvent
import com.github.gaboss44.ecolpr.api.event.transition.TransitionResultEvent
import com.github.gaboss44.ecolpr.api.exception.transition.AsynchronousTransitionException
import com.github.gaboss44.ecolpr.api.exception.transition.ConcurrentTransitionException
import com.github.gaboss44.ecolpr.api.model.road.PrestigeType
import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.EcoLprSettings
import com.github.gaboss44.ecolpr.core.libreforge.trigger.TriggerTransitionAttempt
import com.github.gaboss44.ecolpr.core.libreforge.trigger.TriggerTransitionResult
import com.github.gaboss44.ecolpr.core.util.GroupContext
import com.github.gaboss44.ecolpr.core.model.rank.Rank
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.model.road.Roads
import com.github.gaboss44.ecolpr.core.transition.dto.TransitionDto
import com.github.gaboss44.ecolpr.core.transition.dto.generic.PrestigeDto
import com.github.gaboss44.ecolpr.core.transition.dto.generic.RankupDto
import com.github.gaboss44.ecolpr.core.transition.dto.specific.AscensionDto
import com.github.gaboss44.ecolpr.core.transition.dto.specific.EgressionDto
import com.github.gaboss44.ecolpr.core.transition.dto.specific.IngressionDto
import com.github.gaboss44.ecolpr.core.transition.dto.specific.MigrationDto
import com.github.gaboss44.ecolpr.core.transition.dto.specific.RecursionDto
import com.github.gaboss44.ecolpr.core.util.namedValues
import com.github.gaboss44.ecolpr.core.util.nextOrNull
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

class TransitionManager(private val plugin: EcoLprPlugin) {

    private val locks = ConcurrentHashMap<UUID, ReentrantLock>()

    fun isLocked(player: Player) = this.isLocked(player.uniqueId)

    fun isLocked(uuid: UUID) = this.locks.get(uuid)?.isLocked ?: false

    private fun checkThread() {
        if (!Bukkit.isPrimaryThread()) throw AsynchronousTransitionException(
            "Cannot transition off the main thread."
        )
    }

    private fun checkPlayer(player: Player) {
        if (isLocked(player)) throw ConcurrentTransitionException(
            "Cannot transition player ${player.name}" +
                    " because they are already being transitioned"
        )
    }

    private inline fun <T> Player.withLock(action: () -> T): T {
        val lock = locks.computeIfAbsent(this.uniqueId) { ReentrantLock() }
        lock.lock()
        try {
            return action()
        } finally {
            lock.unlock()
            if (!lock.isLocked) locks.remove(this.uniqueId)
        }
    }

    private fun TransitionDto.Attempt.event(options: Transition.Options) : TransitionAttemptEvent {
        val event = TransitionAttemptEvent(plugin.api, this.proxy)
        if (options.areEventsEnabled()) Bukkit.getPluginManager().callEvent(event)
        if (options.areEffectsEnabled()) road.transitionAttemptEffects?.trigger(
            DispatchedTrigger(
                player.toDispatcher(),
                TriggerTransitionAttempt,
                TriggerData(
                    player = player,
                    event = event
                )
            ).apply { addPlaceholders(this@event.namedValues()) }
        )
        return event
    }

    private fun TransitionDto.Result.event(options: Transition.Options) : TransitionResultEvent {
        val event = TransitionResultEvent(plugin.api, this.proxy)
        if (options.areEventsEnabled()) Bukkit.getPluginManager().callEvent(event)
        if (options.areEffectsEnabled()) road.transitionResultEffects?.trigger(
            DispatchedTrigger(
                player.toDispatcher(),
                TriggerTransitionResult,
                TriggerData(
                    player = player,
                    event = event,
                    value = event.transition.status.triggerValue
                )
            ).apply { addPlaceholders(this@event.namedValues()) }
        )
        return event
    }

    fun rankup(
        player: Player,
        road: Road,
        source: Transition.Source = Transition.Source.NOT_SPECIFIED,
        options: Transition.Options = Transition.Options.normal()
    ): RankupDto.Call {
        checkThread()
        checkPlayer(player)

        return player
            .withLock { callRankup(player, road, source, options) }
            .also { if (EcoLprSettings.isDebug()) plugin.logger.info("Transition Call: $it. Transition Options: $options") }
    }

    private fun callRankup(
        player: Player,
        road: Road,
        source: Transition.Source,
        options: Transition.Options
    ): RankupDto.Call {

        if (road.ranks.isEmpty()) return RankupDto.Call.emptyRoad(player, road, source)

        val ranks = road.getRanks(player)

        if (ranks.size > 1) return RankupDto.Call.ambiguousRank(player, road, source)

        val fromRank = ranks.getOrNull(0)

        return when (fromRank) {
            null -> {
                val toRank = road.ranks.first()
                if (toRank.isGroupActive()) IngressionDto.Call.success(
                    doIngress(
                        player,
                        road,
                        toRank,
                        source,
                        options
                    )
                )
                else IngressionDto.Call.inactiveRank(player, road, toRank, source)
            }

            else -> {
                val toRank = road.ranks.nextOrNull(fromRank)
                    ?: return AscensionDto.Call.lastRank(player, road, fromRank, source)
                if (toRank.isGroupActive()) AscensionDto.Call.success(
                    doAscend(
                        player,
                        road,
                        fromRank,
                        toRank,
                        source,
                        options
                    )
                )
                else AscensionDto.Call.inactiveRank(player, road, fromRank, toRank, source)
            }
        }
    }

    fun ingress(
        player: Player,
        road: Road,
        source: Transition.Source = Transition.Source.NOT_SPECIFIED,
        options: Transition.Options = Transition.Options.normal()
    ): IngressionDto.Call {
        checkThread()
        checkPlayer(player)

        return player
            .withLock { callIngress(player, road, source, options) }
            .also { if (EcoLprSettings.isDebug()) plugin.logger.info("Transition Call: $it. Transition Options: $options") }
    }

    private fun callIngress(
        player: Player,
        road: Road,
        source: Transition.Source,
        options: Transition.Options
    ): IngressionDto.Call {

        if (road.ranks.isEmpty()) return IngressionDto.Call.emptyRoad(player, road, source)

        val ranks = road.getRanks(player)

        if (ranks.size > 1) return IngressionDto.Call.ambiguousRank(player, road, source)

        else if (ranks.isNotEmpty()) return IngressionDto.Call.alreadyOnRoad(player, road, source)

        val toRank = road.ranks.first()

        return if (toRank.isGroupActive()) IngressionDto.Call.success(
            doIngress(
                player,
                road,
                toRank,
                source,
                options
            )
        )
        else IngressionDto.Call.inactiveRank(player, road, toRank, source)
    }

    private fun doIngress(
        player: Player,
        road: Road,
        toRank: Rank,
        source: Transition.Source,
        options: Transition.Options
    ): IngressionDto.Result {
        fun IngressionDto.Attempt.pre() : IngressionDto.Result  {
            val event = this.event(options)

            if (event.isCancelled)
                return IngressionDto.Result.cancelled(this)

            if (road.getRanks(player).isNotEmpty() || !toRank.isGroupActive())
                return IngressionDto.Result.stale(this)

            return IngressionDto.Result.success(this)
        }

        fun IngressionDto.Result.post() : IngressionDto.Result {
            if (this.wasSuccessful()) plugin.luckperms.transferGroup(
                player,
                add = GroupContext(toRank.group, road.contextSet)
            )
            this.event(options)
            return this
        }

        return when (options.mode) {
            Transition.Mode.NORMAL -> IngressionDto.Attempt(
                player = player,
                road = road,
                toRank = toRank,
                source = source,
                mode = options.mode
            ).pre().post()

            Transition.Mode.TEST -> IngressionDto.Attempt(
                player = player,
                road = road,
                toRank = toRank,
                source = source,
                mode = options.mode
            ).pre()

            Transition.Mode.FORCE -> IngressionDto.Result.forced(
                player = player,
                road = road,
                toRank = toRank,
                source = source
            ).post()
        }
    }

    fun ascend(
        player: Player,
        road: Road,
        source: Transition.Source = Transition.Source.NOT_SPECIFIED,
        options: Transition.Options = Transition.Options.normal()
    ): AscensionDto.Call {
        checkThread()
        checkPlayer(player)

        return player
            .withLock { callAscend(player, road, source, options) }
            .also { if (EcoLprSettings.isDebug()) plugin.logger.info("Transition Call: $it. Transition Options: $options") }
    }

    private fun callAscend(
        player: Player,
        road: Road,
        source: Transition.Source,
        options: Transition.Options
    ): AscensionDto.Call {

        if (road.ranks.isEmpty()) return AscensionDto.Call.emptyRoad(player, road, source)

        val ranks = road.getRanks(player)

        if (ranks.size > 1) return AscensionDto.Call.ambiguousRank(player, road, source)

        val fromRank = ranks.getOrNull(0) ?: return AscensionDto.Call.notOnRoad(player, road, source)

        val toRank = road.ranks.nextOrNull(fromRank) ?: return AscensionDto.Call.lastRank(player, road, fromRank, source)

        return if (toRank.isGroupActive()) AscensionDto.Call.success(
            doAscend(
                player,
                road,
                fromRank,
                toRank,
                source,
                options
            )
        )
        else AscensionDto.Call.inactiveRank(player, road, fromRank, toRank, source)
    }

    private fun doAscend(
        player: Player,
        road: Road,
        fromRank: Rank,
        toRank: Rank,
        source: Transition.Source,
        options: Transition.Options
    ): AscensionDto.Result {
        fun AscensionDto.Attempt.pre(): AscensionDto.Result {
            val event = this.event(options)
            if (event.isCancelled) return AscensionDto.Result.cancelled(this)

            val currentRanks = road.getRanks(player)

            if (currentRanks.size != 1 || currentRanks[0] != fromRank || !toRank.isGroupActive())
                return AscensionDto.Result.stale(this)

            return AscensionDto.Result.success(this)
        }

        fun AscensionDto.Result.post(): AscensionDto.Result {
            if (this.wasSuccessful()) plugin.luckperms.transferGroup(
                player,
                remove = GroupContext(fromRank.group, road.contextSet),
                add = GroupContext(toRank.group, road.contextSet)
            )
            this.event(options)
            return this
        }

        return when (options.mode) {
            Transition.Mode.NORMAL -> AscensionDto.Attempt(
                player = player,
                road = road,
                fromRank = fromRank,
                toRank = toRank,
                source = source,
                mode = options.mode
            ).pre().post()

            Transition.Mode.TEST -> AscensionDto.Attempt(
                player = player,
                road = road,
                fromRank = fromRank,
                toRank = toRank,
                source = source,
                mode = options.mode
            ).pre()

            Transition.Mode.FORCE -> AscensionDto.Result.forced(
                player = player,
                road = road,
                fromRank = fromRank,
                toRank = toRank,
                source = source
            ).post()
        }
    }

    fun prestige(
        player: Player,
        road: Road,
        source: Transition.Source = Transition.Source.NOT_SPECIFIED,
        options: Transition.Options = Transition.Options.normal()
    ): PrestigeDto.Call {
        checkThread()
        checkPlayer(player)

        return player
            .withLock { callPrestige(player, road, source, options) }
            .also { if (EcoLprSettings.isDebug()) plugin.logger.info("Transition Call: $it. Transition Options: $options") }
    }

    private fun callPrestige(
        player: Player,
        road: Road,
        source: Transition.Source,
        options: Transition.Options
    ): PrestigeDto.Call {
        val type = road.prestigeType ?: return PrestigeDto.Call.notSpecified(player, road, source)
        return when (type) {
            PrestigeType.EGRESSION -> callEgress(player, road, source, options)
            PrestigeType.RECURSION -> callRecurse(player, road, source, options)
            PrestigeType.MIGRATION -> callMigrate(player, road, source, options)
        }
    }

    fun egress(
        player: Player,
        road: Road,
        source: Transition.Source = Transition.Source.NOT_SPECIFIED,
        options: Transition.Options = Transition.Options.normal()
    ): EgressionDto.Call {
        checkThread()
        checkPlayer(player)

        return player
            .withLock { callEgress(player, road, source, options) }
            .also { if (EcoLprSettings.isDebug()) plugin.logger.info("Transition Call: $it. Transition Options: $options") }
    }

    private fun callEgress(
        player: Player,
        road: Road,
        source: Transition.Source,
        options: Transition.Options
    ): EgressionDto.Call {

        if (road.ranks.isEmpty()) return EgressionDto.Call.emptyRoad(player, road, source)

        val ranks = road.getRanks(player)

        if (ranks.size > 1) return EgressionDto.Call.ambiguousRank(player, road, source)

        val fromRank = ranks.getOrNull(0) ?: return EgressionDto.Call.notOnRoad(player, road, source)

        if (fromRank != road.ranks.last()) return EgressionDto.Call.notLastRank(player, road, fromRank, source)

        return EgressionDto.Call.success(
            doEgress(
                player,
                road,
                fromRank,
                source,
                options
            )
        )
    }

    private fun doEgress(
        player: Player,
        road: Road,
        fromRank: Rank,
        source: Transition.Source,
        options: Transition.Options
    ): EgressionDto.Result {
        fun EgressionDto.Attempt.pre(): EgressionDto.Result {
            val event = this.event(options)
            if (event.isCancelled) return EgressionDto.Result.cancelled(this)

            val currentRanks = road.getRanks(player)

            if (currentRanks.size != 1 || currentRanks[0] != fromRank)
                return EgressionDto.Result.stale(this)

            return EgressionDto.Result.success(this)
        }

        fun EgressionDto.Result.post(): EgressionDto.Result {
            if (this.wasSuccessful()) {
                plugin.luckperms.transferGroup(
                    player,
                    remove = GroupContext(fromRank.group, road.contextSet)
                )
                road.setPrestigeLevel(player, this.prestigeLevel)
            }
            this.event(options)
            return this
        }

        return when (options.mode) {
            Transition.Mode.NORMAL -> EgressionDto.Attempt(
                player = player,
                road = road,
                fromRank = fromRank,
                prestigeLevel = road.getPrestigeLevel(player) + 1,
                source = source,
                mode = options.mode
            ).pre().post()

            Transition.Mode.TEST -> EgressionDto.Attempt(
                player = player,
                road = road,
                fromRank = fromRank,
                prestigeLevel = road.getPrestigeLevel(player) + 1,
                source = source,
                mode = options.mode
            ).pre()

            Transition.Mode.FORCE -> EgressionDto.Result.forced(
                player = player,
                road = road,
                fromRank = fromRank,
                prestigeLevel = road.getPrestigeLevel(player) + 1,
                source = source
            ).post()
        }
    }

    fun recurse(
        player: Player,
        road: Road,
        source: Transition.Source = Transition.Source.NOT_SPECIFIED,
        options: Transition.Options = Transition.Options.normal()
    ): RecursionDto.Call {
        checkThread()
        checkPlayer(player)

        return player
            .withLock { callRecurse(player, road, source, options) }
            .also { if (EcoLprSettings.isDebug()) plugin.logger.info("Transition Call: $it. Transition Options: $options") }
    }

    private fun callRecurse(
        player: Player,
        road: Road,
        source: Transition.Source,
        options: Transition.Options
    ): RecursionDto.Call {

        if (road.ranks.isEmpty()) return RecursionDto.Call.emptyRoad(player, road, source)

        val ranks = road.getRanks(player)

        if (ranks.size > 1) return RecursionDto.Call.ambiguousRank(player, road, source)

        val fromRank = ranks.getOrNull(0) ?: return RecursionDto.Call.notOnRoad(player, road, source)

        if (fromRank != road.ranks.last()) return RecursionDto.Call.notLastRank(player, road, fromRank, source)

        val toRank = road.ranks.first()

        return if (toRank.isGroupActive()) RecursionDto.Call.success(
            doRecurse(
                player,
                road,
                fromRank,
                toRank,
                source,
                options
            )
        )
        else RecursionDto.Call.inactiveRank(player, road, fromRank, toRank, source)
    }

    private fun doRecurse(
        player: Player,
        road: Road,
        fromRank: Rank,
        toRank: Rank,
        source: Transition.Source,
        options: Transition.Options
    ): RecursionDto.Result {
        fun RecursionDto.Attempt.pre(): RecursionDto.Result {
            val event = this.event(options)

            if (event.isCancelled)
                return RecursionDto.Result.cancelled(this)

            val currentRanks = road.getRanks(player)

            if (currentRanks.size != 1 || currentRanks[0] != fromRank || !toRank.isGroupActive())
                return RecursionDto.Result.stale(this)

            return RecursionDto.Result.success(this)
        }

        fun RecursionDto.Result.post(): RecursionDto.Result {
            if (this.wasSuccessful()) {
                plugin.luckperms.transferGroup(
                    player,
                    remove = GroupContext(fromRank.group, road.contextSet),
                    add = GroupContext(toRank.group, road.contextSet)
                )
                road.setPrestigeLevel(player, this.prestigeLevel)
            }
            this.event(options)
            return this
        }

        return when (options.mode) {
            Transition.Mode.NORMAL -> RecursionDto.Attempt(
                player = player,
                road = road,
                fromRank = fromRank,
                toRank = toRank,
                prestigeLevel = road.getPrestigeLevel(player) + 1,
                source = source,
                mode = options.mode
            ).pre().post()

            Transition.Mode.TEST -> RecursionDto.Attempt(
                player = player,
                road = road,
                fromRank = fromRank,
                toRank = toRank,
                prestigeLevel = road.getPrestigeLevel(player) + 1,
                source = source,
                mode = options.mode
            ).pre()

            Transition.Mode.FORCE -> RecursionDto.Result.forced(
                player = player,
                road = road,
                fromRank = fromRank,
                toRank = toRank,
                prestigeLevel = road.getPrestigeLevel(player) + 1,
                source = source
            ).post()
        }
    }

    fun migrate(
        player: Player,
        road: Road,
        source: Transition.Source = Transition.Source.NOT_SPECIFIED,
        options: Transition.Options = Transition.Options.normal()
    ): MigrationDto.Call {
        checkThread()
        checkPlayer(player)

        return player
            .withLock { callMigrate(player, road, source, options) }
            .also { if (EcoLprSettings.isDebug()) plugin.logger.info("Transition Call: $it. Transition Options: $options") }
    }

    private fun callMigrate(
        player: Player,
        road: Road,
        source: Transition.Source,
        options: Transition.Options
    ): MigrationDto.Call {

        if (road.ranks.isEmpty()) return MigrationDto.Call.emptyRoad(player, road, source)

        val ranks = road.getRanks(player)

        if (ranks.size > 1) return MigrationDto.Call.ambiguousRank(player, road, source)

        val fromRank = ranks.getOrNull(0) ?: return MigrationDto.Call.notOnRoad(player, road, source)

        if (fromRank != road.ranks.last()) return MigrationDto.Call.notLastRank(player, road, fromRank, source)

        val prestigeTarget = options.prestigeTarget ?: road.prestigeTarget

        val prestigeRoad = Roads[prestigeTarget] ?: return MigrationDto.Call.noPrestigeRoad(player, road, fromRank, source)

        if (prestigeRoad.ranks.isEmpty()) return MigrationDto.Call.emptyPrestigeRoad(player, road, fromRank, prestigeRoad, source)

        val otherRanks = prestigeRoad.getRanks(player)

        if (otherRanks.size > 1) return MigrationDto.Call.ambiguousPrestigeRank(player, prestigeRoad, fromRank, prestigeRoad, source)

        if (otherRanks.isNotEmpty()) return MigrationDto.Call.alreadyOnPrestigeRoad(player, road, fromRank, prestigeRoad, source)

        val toRank = prestigeRoad.ranks.first()

        return if (toRank.isGroupActive()) MigrationDto.Call.success(
            doMigrate(
                player,
                road,
                fromRank,
                toRank,
                prestigeRoad,
                source,
                options
            )
        )
        else MigrationDto.Call.inactivePrestigeRank(player, road, fromRank, toRank, prestigeRoad, source)
    }

    private fun doMigrate(
        player: Player,
        road: Road,
        fromRank: Rank,
        toRank: Rank,
        prestigeRoad: Road,
        source: Transition.Source,
        options: Transition.Options
    ): MigrationDto.Result {
        fun MigrationDto.Attempt.pre(): MigrationDto.Result {
            val event = this.event(options)

            if (event.isCancelled)
                return MigrationDto.Result.cancelled(this)

            val currentRanks = road.getRanks(player)

            if (currentRanks.size != 1 || currentRanks[0] != fromRank ||
                !toRank.isGroupActive() || prestigeRoad.getRanks(player).isNotEmpty())
                return MigrationDto.Result.stale(this)

            return MigrationDto.Result.success(this)
        }

        fun MigrationDto.Result.post(): MigrationDto.Result {
            if (this.wasSuccessful()) {
                plugin.luckperms.transferGroup(
                    player,
                    remove = GroupContext(fromRank.group, road.contextSet),
                    add = GroupContext(toRank.group, prestigeRoad.contextSet)
                )
                road.setPrestigeLevel(player, this.prestigeLevel)
            }
            this.event(options)
            return this
        }

        return when (options.mode) {
            Transition.Mode.NORMAL -> MigrationDto.Attempt(
                player = player,
                road = road,
                prestigeRoad = prestigeRoad,
                fromRank = fromRank,
                toRank = toRank,
                prestigeLevel = road.getPrestigeLevel(player) + 1,
                source = source,
                mode = options.mode
            ).pre().post()

            Transition.Mode.TEST -> MigrationDto.Attempt(
                player = player,
                road = road,
                prestigeRoad = prestigeRoad,
                fromRank = fromRank,
                toRank = toRank,
                prestigeLevel = road.getPrestigeLevel(player) + 1,
                source = source,
                mode = options.mode
            ).pre()

            Transition.Mode.FORCE -> MigrationDto.Result.forced(
                player = player,
                road = road,
                prestigeRoad = prestigeRoad,
                fromRank = fromRank,
                toRank = toRank,
                prestigeLevel = road.getPrestigeLevel(player) + 1,
                source = source
            ).post()
        }
    }
}
