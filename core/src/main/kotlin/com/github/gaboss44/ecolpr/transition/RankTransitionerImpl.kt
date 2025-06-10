package com.github.gaboss44.ecolpr.transition

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.event.PlayerPostRankDemotionEvent
import com.github.gaboss44.ecolpr.event.PlayerPostRankGraduationEvent
import com.github.gaboss44.ecolpr.event.PlayerPostRankPromotionEvent
import com.github.gaboss44.ecolpr.event.PlayerPostRankRebootEvent
import com.github.gaboss44.ecolpr.event.PlayerPostRankTransitionEvent
import com.github.gaboss44.ecolpr.event.PlayerRankDemotionEvent
import com.github.gaboss44.ecolpr.event.PlayerRankGraduationEvent
import com.github.gaboss44.ecolpr.event.PlayerRankPromotionEvent
import com.github.gaboss44.ecolpr.event.PlayerRankRebootEvent
import com.github.gaboss44.ecolpr.event.PlayerRankTransitionEvent
import com.github.gaboss44.ecolpr.exception.ConcurrentRankTransitionException
import com.github.gaboss44.ecolpr.exception.IllegalRoadException
import com.github.gaboss44.ecolpr.exception.LastRankInRoadException
import com.github.gaboss44.ecolpr.exception.NoRanksInRoadException
import com.github.gaboss44.ecolpr.exception.NotInMainThreadException
import com.github.gaboss44.ecolpr.exception.NotLastRankInRoadException
import com.github.gaboss44.ecolpr.exception.NotSatisfiedByAnyRoadException
import com.github.gaboss44.ecolpr.exception.NotTravelingRoadException
import com.github.gaboss44.ecolpr.exception.PrestigeMaxLevelException
import com.github.gaboss44.ecolpr.exception.PrestigeWithoutLevelsException
import com.github.gaboss44.ecolpr.exception.RoadWithoutPrestigeException
import com.github.gaboss44.ecolpr.libreforge.trigger.TriggerPostRankTransition
import com.github.gaboss44.ecolpr.libreforge.trigger.TriggerRankTransition
import com.github.gaboss44.ecolpr.prestige.Prestige
import com.github.gaboss44.ecolpr.road.Road
import com.github.gaboss44.ecolpr.road.RoadImpl
import com.github.gaboss44.ecolpr.wrapper.UserWrapper
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class RankTransitionerImpl(private val plugin: EcoLprPlugin) : RankTransitioner {

    private val locks: ConcurrentHashMap<UUID, ReentrantLock> = ConcurrentHashMap()

    override fun isLocked(uuid: UUID): Boolean =
        locks[uuid]?.isLocked ?: false

    /* API promotion */

    override fun promote(player: Player) : RankTransitionResult<RankPromotion> {
        validateTransitionPrerequisites(player)
        return doPromote(
            player,
            plugin.configYml.getBool("select-most-satisfactory-road"),
            RankTransition.Source.API
        )
    }

    override fun promote(player: Player, selectMostSatisfactoryRoad: Boolean): RankTransitionResult<RankPromotion> {
        validateTransitionPrerequisites(player)
        return doPromote(
            player,
            selectMostSatisfactoryRoad,
            RankTransition.Source.API
        )
    }

    override fun promote(player: Player, road: Road): RankTransitionResult<RankPromotion> {
        validateTransitionPrerequisites(player)
        if (road !is RoadImpl) throw IllegalRoadException(
            road,
            "Cannot promote player ${player.name} in road ${road.id} because it was not obtained from EcoLPR."
        )
        return doPromote(player, road, RankTransition.Source.API)
    }

    /* Internal promotion */

    fun promote(player: Player, source: RankTransition.Source): RankTransitionResult<RankPromotion> {
        validateTransitionPrerequisites(player)
        return doPromote(
            player,
            plugin.configYml.getBool("select-most-satisfactory-road"),
            source
        )
    }

    fun promote(
        player: Player,
        selectMostSatisfactoryRoad: Boolean,
        source: RankTransition.Source
    ): RankTransitionResult<RankPromotion> {
        validateTransitionPrerequisites(player)
        return doPromote(
            player,
            selectMostSatisfactoryRoad,
            source
        )
    }

    fun promote(
        player: Player,
        road: Road,
        source: RankTransition.Source
    ): RankTransitionResult<RankPromotion> {
        validateTransitionPrerequisites(player)
        if (road !is RoadImpl) throw IllegalRoadException(
            road,
            "Cannot promote player ${player.name} in road ${road.id} because it was not obtained from EcoLPR."
        )
        return doPromote(player, road, source)
    }

    /* Private promotion */

    private fun doPromote(
        player: Player,
        selectMostSatisfactoryRoad: Boolean,
        source: RankTransition.Source
    ): RankTransitionResult<RankPromotion> {
        val selectedRoad = selectRoadForPlayer(player, selectMostSatisfactoryRoad)
        return doPromote(player, selectedRoad, source)
    }

    private fun doPromote(
        player: Player,
        road: Road,
        source: RankTransition.Source
    ): RankTransitionResult<RankPromotion> {
        val promotion = makePromotion(player, road, source)
        return executeTransition(
            player, promotion,
            ::PlayerRankPromotionEvent,
            ::PlayerPostRankPromotionEvent
        ) { transition, user ->
            val context = road.queryOptions.context()
            transition.from?.let {
                val fromNode = InheritanceNode.builder(it.idOverride).context(context).build()
                user.data.remove(fromNode)
            }
            val toNode = InheritanceNode.builder(transition.to.idOverride).value(true).context(context).build()
            user.data.add(toNode)
        }
    }

    private fun makePromotion(player: Player, road: Road, source: RankTransition.Source): RankPromotion {
        val ranks = plugin.rankResolver.getOrderedRanks(road).also {
            if (it.isEmpty()) {
                throw NoRanksInRoadException(
                    road,
                    "Cannot promote player ${player.name} in road ${road.id} because no ranks were found."
                )
            }
        }
        val from = ranks.firstOrNull { it.isHeldBy(player) }
        val to = if (from == null) ranks.first()
        else if (ranks.isLast(from)) throw LastRankInRoadException(
            road,
            from,
            "Cannot promote player ${player.name} in road ${road.id} because they are already at the last rank."
        )
        else ranks.getNext(from)!!
        val status = when {
            !road.genericConditions.areMet(player.toDispatcher(), EmptyProvidedHolder) -> RankTransition.Status.ROAD_GENERIC_CONDITIONS_FAILURE
            !road.promotionConditions.areMet(player.toDispatcher(), EmptyProvidedHolder) -> RankTransition.Status.ROAD_PROMOTION_CONDITIONS_FAILURE
            else -> RankTransition.Status.SUCCESS
        }
        return RankPromotionImpl(player, source, status, road, from, to)
    }

    /* API demotion */

    override fun demote(player: Player) : RankTransitionResult<RankDemotion> {
        validateTransitionPrerequisites(player)
        return doDemote(
            player,
            plugin.configYml.getBool("select-most-satisfactory-road"),
            RankTransition.Source.API)
    }

    override fun demote(player: Player, selectMostSatisfactoryRoad: Boolean): RankTransitionResult<RankDemotion> {
        validateTransitionPrerequisites(player)
        return doDemote(
            player,
            selectMostSatisfactoryRoad,
            RankTransition.Source.API)
    }

    override fun demote(player: Player, road: Road): RankTransitionResult<RankDemotion> {
        validateTransitionPrerequisites(player)
        if (road !is RoadImpl) throw IllegalRoadException(
            road,
            "Cannot demote player ${player.name} in road ${road.id} because it was not obtained from EcoLPR."
        )
        return doDemote(player, road, RankTransition.Source.API)
    }

    /* Internal demotion */

    fun demote(player: Player, source: RankTransition.Source): RankTransitionResult<RankDemotion> {
        validateTransitionPrerequisites(player)
        return doDemote(
            player,
            plugin.configYml.getBool("select-most-satisfactory-road"),
            source)
    }

    fun demote(
        player: Player,
        selectMostSatisfactoryRoad: Boolean,
        source: RankTransition.Source
    ): RankTransitionResult<RankDemotion> {
        validateTransitionPrerequisites(player)
        return doDemote(player,
            selectMostSatisfactoryRoad,
            source)
    }

    fun demote(
        player: Player,
        road: Road,
        source: RankTransition.Source
    ): RankTransitionResult<RankDemotion> {
        validateTransitionPrerequisites(player)
        if (road !is RoadImpl) throw IllegalRoadException(
            road,
            "Cannot demote player ${player.name} in road ${road.id} because it was not obtained from EcoLPR."
        )
        return doDemote(player, road, source)
    }

    /* Private demotion */

    private fun doDemote(
        player: Player,
        selectMostSatisfactoryRoad: Boolean,
        source: RankTransition.Source
    ): RankTransitionResult<RankDemotion> {
        val roads = plugin.roadResolver.getSatisfactoryRoads(player).apply {
            if (isEmpty()) {
                throw NotSatisfiedByAnyRoadException(
                    "Cannot demote player ${player.name} because they are not in any satisfactory road."
                )
            }
        }
        val selectedRoad = selectRoadForPlayer(player, selectMostSatisfactoryRoad)
        return doDemote(player, selectedRoad, source)
    }

    private fun doDemote(
        player: Player,
        road: Road,
        source: RankTransition.Source
    ): RankTransitionResult<RankDemotion> {
        val demotion = makeDemotion(player, road, source)
        return executeTransition(
            player, demotion,
            ::PlayerRankDemotionEvent,
            ::PlayerPostRankDemotionEvent
        ) { transition, user ->
            val context = road.queryOptions.context()
            transition.to?.let {
                val toNode = InheritanceNode.builder(it.idOverride).value(true).context(context).build()
                user.data.add(toNode)
            }
            val fromNode = InheritanceNode.builder(transition.from.idOverride).context(context).build()
            user.data.add(fromNode)
        }
    }

    private fun makeDemotion(player: Player, road: Road, source: RankTransition.Source): RankDemotion {
        val ranks = plugin.rankResolver.getOrderedRanks(road).also {
            if (it.isEmpty()) {
                throw NoRanksInRoadException(
                    road,
                    "Cannot demote player ${player.name} in road ${road.id} because no ranks were found."
                )
            }
        }
        val from = ranks.firstOrNull { it.isHeldBy(player) } ?: throw NotTravelingRoadException(
            road,
            "Cannot demote player ${player.name} in road ${road.id} because they have no rank in the road"
        )
        val to = ranks.getPrevious(from)
        val status = when {
            !road.genericConditions.areMet(player.toDispatcher(), EmptyProvidedHolder) -> RankTransition.Status.ROAD_GENERIC_CONDITIONS_FAILURE
            !road.demotionConditions.areMet(player.toDispatcher(), EmptyProvidedHolder) -> RankTransition.Status.ROAD_DEMOTION_CONDITIONS_FAILURE
            else -> RankTransition.Status.SUCCESS
        }
        return RankDemotionImpl(player, source, status, road, from, to)
    }

    /* API graduation */

    override fun graduate(player: Player): RankTransitionResult<RankGraduation> {
        validateTransitionPrerequisites(player)
        return doGraduate(
            player,
            plugin.configYml.getBool("select-most-satisfactory-road"),
            RankTransition.Source.API)
    }

    override fun graduate(player: Player, selectMostSatisfactoryRoad: Boolean): RankTransitionResult<RankGraduation> {
        validateTransitionPrerequisites(player)
        return doGraduate(
            player,
            selectMostSatisfactoryRoad,
            RankTransition.Source.API)
    }

    override fun graduate(player: Player, road: Road): RankTransitionResult<RankGraduation> {
        validateTransitionPrerequisites(player)
        if (road !is RoadImpl) throw IllegalRoadException(
            road,
            "Cannot graduate from road ${road.id} because it was not obtained from EcoLPR."
        )
        return doGraduate(player, road, road.next, RankTransition.Source.API)
    }

    override fun graduate(player: Player, selectMostSatisfactoryRoad: Boolean, nextRoad: Road?): RankTransitionResult<RankGraduation> {
        validateTransitionPrerequisites(player)
        if (nextRoad != null && nextRoad !is RoadImpl) throw IllegalRoadException(
            nextRoad,
            "Cannot graduate to road ${nextRoad.id} because it was not obtained from EcoLPR."
        )
        return doGraduate(player, selectMostSatisfactoryRoad, nextRoad, RankTransition.Source.API)
    }

    override fun graduate(player: Player, road: Road, nextRoad: Road?): RankTransitionResult<RankGraduation> {
        validateTransitionPrerequisites(player)
        if (road !is RoadImpl) throw IllegalRoadException(
            road,
            "Cannot graduate from road ${road.id} because it was not obtained from EcoLPR."
        )
        if (nextRoad != null && nextRoad !is RoadImpl) throw IllegalRoadException(
            nextRoad,
            "Cannot graduate to road ${nextRoad.id} because it was not obtained from EcoLPR."
        )
        return doGraduate(player, road, nextRoad, RankTransition.Source.API)
    }

    /* Internal graduation */

    fun graduate(player: Player, source: RankTransition.Source): RankTransitionResult<RankGraduation> {
        validateTransitionPrerequisites(player)
        return doGraduate(
            player,
            plugin.configYml.getBool("select-most-satisfactory-road"),
            source
        )
    }

    fun graduate(
        player: Player,
        selectMostSatisfactoryRoad: Boolean,
        source: RankTransition.Source
    ): RankTransitionResult<RankGraduation> {
        validateTransitionPrerequisites(player)
        return doGraduate(player, selectMostSatisfactoryRoad, source)
    }

    fun graduate(
        player: Player,
        selectMostSatisfactoryRoad: Boolean,
        nextRoad: Road?,
        source: RankTransition.Source
    ): RankTransitionResult<RankGraduation> {
        validateTransitionPrerequisites(player)
        return doGraduate(player, selectMostSatisfactoryRoad, nextRoad, source)
    }

    fun graduate(
        player: Player,
        road: Road,
        nextRoad: Road?,
        source: RankTransition.Source
    ): RankTransitionResult<RankGraduation> {
        validateTransitionPrerequisites(player)
        return doGraduate(player, road, nextRoad, source)
    }

    /* Private graduation */

    private fun doGraduate(
        player: Player,
        selectMostSatisfactoryRoad: Boolean,
        source: RankTransition.Source
    ): RankTransitionResult<RankGraduation> {
        val selectedRoad = selectRoadForPlayer(player, selectMostSatisfactoryRoad)
        return doGraduate(player, selectedRoad, selectedRoad.next, source)
    }

    private fun doGraduate(
        player: Player,
        selectMostSatisfactoryRoad: Boolean,
        nextRoad: Road?,
        source: RankTransition.Source
    ): RankTransitionResult<RankGraduation> {
        val currentRoad = selectRoadForPlayer(player, selectMostSatisfactoryRoad)
        return doGraduate(player, currentRoad, nextRoad, source)
    }

    private fun doGraduate(
        player: Player,
        road: Road,
        nextRoad: Road?,
        source: RankTransition.Source
    ): RankTransitionResult<RankGraduation> {
        val graduation = makeGraduation(player, road, nextRoad, source)
        return executeTransition(
            player, graduation,
            ::PlayerRankGraduationEvent,
            ::PlayerPostRankGraduationEvent
        ) { transition, user ->
            val context = road.queryOptions.context()
            val fromNode = InheritanceNode.builder(transition.from.idOverride)
                .context(context)
                .build()
            user.data.remove(fromNode)
            transition.to?.let {
                val toNode = InheritanceNode.builder(it.idOverride)
                    .value(true)
                    .context(nextRoad!!.queryOptions.context())
                    .build()
                user.data.add(toNode)
            }
            plugin.repository.getTrack(transition.prestige.idOverride)?.promote(user, context)
        }
    }

    private fun makeGraduation(
        player: Player,
        road: Road,
        nextRoad: Road?,
        source: RankTransition.Source
    ): RankGraduation {
        val currentRanks = plugin.rankResolver.getOrderedRanks(road).also {
            if (it.isEmpty())
                throw NoRanksInRoadException(
                    road,
                    "Cannot graduate player ${player.name}: no ranks in road ${road.id}."
                )
        }

        val from = currentRanks.firstOrNull { it.isHeldBy(player) } ?: throw NotTravelingRoadException(
            road,
            "Cannot graduate player ${player.name}: no rank in current road ${road.id}."
        )

        if (!currentRanks.isLast(from))
            throw NotLastRankInRoadException(
                road,
                from,
                "Player ${player.name} is not at the last rank of road ${road.id}."
            )

        val to = nextRoad?.let {
            val nextRanks = plugin.rankResolver.getOrderedRanks(it).also { nr ->
                if (nr.isEmpty())
                    throw NoRanksInRoadException(it, "Next road ${it.id} has no ranks.")
            }
            nextRanks.first()
        }

        val level: Int
        val prestige = road.prestige?.also {
            val maxLevel = it.maxLevel
            if (maxLevel == -1) throw PrestigeWithoutLevelsException(
                it,
                "Cannot graduate ${player.name} from road ${road.id} because prestige ${it.id} has no levels"
            )
            level = it.getLevel(player, road)
            if (level == maxLevel) throw PrestigeMaxLevelException(
                it,
                level,
                "Cannot graduate ${player.name} from road ${road.id} because they have reached the max level for prestige ${it.id}"
            )
        } ?: throw RoadWithoutPrestigeException(
            road,
            "Cannot graduate ${player.name} from road ${road.id} because the later has no prestige"
        )

        val status = when {
            !road.genericConditions.areMet(player.toDispatcher(), EmptyProvidedHolder) ->
                RankTransition.Status.ROAD_GENERIC_CONDITIONS_FAILURE
            !road.graduationConditions.areMet(player.toDispatcher(), EmptyProvidedHolder) ->
                RankTransition.Status.ROAD_GRADUATION_CONDITIONS_FAILURE
            nextRoad != null && !nextRoad.genericConditions.areMet(player.toDispatcher(), EmptyProvidedHolder) ->
                RankTransition.Status.NEXT_ROAD_GENERIC_CONDITIONS_FAILURE
            else -> RankTransition.Status.SUCCESS
        }

        return RankGraduationImpl(player, source, status, road, nextRoad, from, to, prestige, level)
    }

    /* API reboot */

    override fun reboot(player: Player): RankTransitionResult<RankReboot> {
        validateTransitionPrerequisites(player)
        return doReboot(
            player,
            plugin.configYml.getBool("select-most-satisfactory-road"),
            RankTransition.Source.API)
    }

    override fun reboot(player: Player, selectMostSatisfactoryRoad: Boolean): RankTransitionResult<RankReboot> {
        validateTransitionPrerequisites(player)
        return doReboot(
            player,
            selectMostSatisfactoryRoad,
            RankTransition.Source.API
        )
    }

    override fun reboot(player: Player, road: Road): RankTransitionResult<RankReboot> {
        validateTransitionPrerequisites(player)
        if (road !is RoadImpl)
            throw IllegalRoadException(road, "Cannot reboot player ${player.name} in road ${road.id} because it was not obtained from EcoLPR.")
        return doReboot(player, road, RankTransition.Source.API)
    }

    /* Internal reboot */

    fun reboot(player: Player, source: RankTransition.Source): RankTransitionResult<RankReboot> {
        validateTransitionPrerequisites(player)
        return doReboot(
            player,
            plugin.configYml.getBool("select-most-satisfactory-road"),
            source
        )
    }

    fun reboot(
        player: Player,
        selectMostSatisfactoryRoad: Boolean,
        source: RankTransition.Source
    ): RankTransitionResult<RankReboot> {
        validateTransitionPrerequisites(player)
        return doReboot(player, selectMostSatisfactoryRoad, source)
    }

    fun reboot(
        player: Player,
        road: Road,
        source: RankTransition.Source
    ): RankTransitionResult<RankReboot> {
        validateTransitionPrerequisites(player)
        return doReboot(player, road, source)
    }

    /* Private reboot */

    private fun doReboot(
        player: Player,
        selectMostSatisfactoryRoad: Boolean,
        source: RankTransition.Source
    ): RankTransitionResult<RankReboot> {
        val selectedRoad = selectRoadForPlayer(player, selectMostSatisfactoryRoad)
        return doReboot(player, selectedRoad, source)
    }

    private fun doReboot(
        player: Player,
        road: Road,
        source: RankTransition.Source
    ): RankTransitionResult<RankReboot> {
        val reboot = makeReboot(player, road, source)
        return executeTransition(
            player, reboot,
            ::PlayerRankRebootEvent,
            ::PlayerPostRankRebootEvent
        ) { transition, user ->
            val context = road.queryOptions.context()
            val fromNode = InheritanceNode.builder(transition.from.idOverride).context(context).build()
            user.data.remove(fromNode)
            val toNode = InheritanceNode.builder(transition.to.idOverride).value(true).context(context).build()
            user.data.add(toNode)
            plugin.repository.getTrack(transition.prestige.idOverride)?.promote(user, context)
        }
    }

    private fun makeReboot(
        player: Player,
        road: Road,
        source: RankTransition.Source
    ): RankReboot {
        val ranks = plugin.rankResolver.getOrderedRanks(road).also {
            if (it.isEmpty()) {
                throw NoRanksInRoadException(
                    road,
                    "Cannot reboot player ${player.name} in road ${road.id} because no ranks were found."
                )
            }
        }

        val from = ranks.firstOrNull { it.isHeldBy(player) } ?: throw NotTravelingRoadException(
            road,
            "Cannot reboot player ${player.name} in road ${road.id} because they have no rank in the road."
        )

        val level: Int
        val prestige = road.prestige?.also {
            val maxLevel = it.maxLevel
            if (maxLevel == -1) throw PrestigeWithoutLevelsException(
                it,
                "Cannot reboot ${player.name} in road ${road.id} because prestige ${it.id} has no levels"
            )
            level = it.getLevel(player, road)
            if (level == maxLevel) throw PrestigeMaxLevelException(
                it,
                level,
                "Cannot reboot ${player.name} in road ${road.id} because they have reached the max level for prestige ${it.id}"
            )
        } ?: throw RoadWithoutPrestigeException(
            road,
            "Cannot reboot ${player.name} from road ${road.id} because the later has no prestige"
        )


        val to = ranks.first()
        val status = when {
            !road.genericConditions.areMet(player.toDispatcher(), EmptyProvidedHolder) -> RankTransition.Status.ROAD_GENERIC_CONDITIONS_FAILURE
            !road.rebootConditions.areMet(player.toDispatcher(), EmptyProvidedHolder) -> RankTransition.Status.ROAD_REBOOT_CONDITIONS_FAILURE
            else -> RankTransition.Status.SUCCESS
        }

        return RankRebootImpl(player, source, status, road, from, to, prestige, level)
    }

    /* Utils */

    private fun validateTransitionPrerequisites(player: Player) {
        if (isLocked(player)) {
            throw ConcurrentRankTransitionException(
                "Cannot transition player ${player.name} because they are already being transitioned or having their data saved."
            )
        }
        if (!Bukkit.isPrimaryThread()) {
            throw NotInMainThreadException(
                "Cannot transition player ${player.name} or any other off the main thread."
            )
        }
    }

    private fun selectRoadForPlayer(player: Player, selectMostSatisfactory: Boolean): Road {
        val roads = plugin.roadResolver.getSatisfactoryRoads(player).apply {
            if (isEmpty()) {
                throw NotSatisfiedByAnyRoadException(
                    "Cannot transition player ${player.name} because they are not in any satisfactory road."
                )
            }
        }
        return if (selectMostSatisfactory) roads.first() else roads.last()
    }

    private fun <T : RankTransition> executeTransition(
        player: Player,
        transition: T,
        eventFactory: (T) -> PlayerRankTransitionEvent,
        postEventFactory: (T) -> PlayerPostRankTransitionEvent,
        applyChanges: (transition: T, user: UserWrapper) -> Unit
    ): RankTransitionResult<T> {
        val event = eventFactory(transition)
        val lock = locks.computeIfAbsent(player.uniqueId) { ReentrantLock() }

        lock.lock()
        try {
            Bukkit.getPluginManager().callEvent(event)

            // Pre-event trigger
            transition.road.rankTransitionEffects?.trigger(
                DispatchedTrigger(
                    player.toDispatcher(),
                    TriggerRankTransition,
                    TriggerData(
                        player = player,
                        event = event,
                        value = event.transition.status.triggerValue
                    )
                ).apply {
                    addPlaceholder(NamedValue("from_rank", transition.from?.id ?: ""))
                    addPlaceholder(NamedValue("to_rank", transition.to?.id ?: ""))
                    addPlaceholder(NamedValue("from_rank_display", transition.from?.displayName?.toNiceString() ?: ""))
                    addPlaceholder(NamedValue("to_rank_display", transition.to?.displayName?.toNiceString() ?: ""))
                }
            )

            if (event.isCancelled || !transition.status.isSuccess) {
                lock.unlock()
                locks.remove(player.uniqueId)
                return RankTransitionResultImpl(transition, true)
            }

            // Post-event
            val postEvent = postEventFactory(transition)
            Bukkit.getPluginManager().callEvent(postEvent)

            transition.road.postRankTransitionEffects?.trigger(
                DispatchedTrigger(
                    player.toDispatcher(),
                    TriggerPostRankTransition,
                    TriggerData(
                        player = player,
                        event = postEvent,
                        value = postEvent.transition.status.triggerValue
                    )
                )
            )

            val user = plugin.repository.getUser(player)
            applyChanges(transition, user)

            val save = plugin.repository.saveUser(user)
            val timeout = plugin.configYml.getIntOrNull("save-user-timeout-before-unlocking-millis")
                ?.takeIf { it >= 0 }?.toLong()
                ?.let { createTimeout(it) }

            if (timeout == null) {
                save.whenComplete { _, _ ->
                    plugin.logger.info("${player.name}'s user data has been successfully saved.")
                    lock.unlock()
                    locks.remove(player.uniqueId)
                }
            } else {
                CompletableFuture.anyOf(save, timeout).whenComplete { obj, _ ->
                    if (obj is TimeoutObject)
                        plugin.logger.warning("${player.name}'s user data has not been completely saved after ${timeout}ms. Unlocking...")
                    else
                        plugin.logger.info("${player.name}'s user data has been successfully saved.")
                    lock.unlock()
                    locks.remove(player.uniqueId)
                }
            }

            return RankTransitionResultImpl(transition, false)

        } catch (e: Exception) {
            lock.unlock()
            locks.remove(player.uniqueId)
            throw e
        }
    }

    private fun createTimeout(ms: Long): CompletableFuture<TimeoutObject> =
        createTimeout(ms, TimeoutObject)

    private fun <T> createTimeout(ms: Long, obj: T): CompletableFuture<T> =
        CompletableFuture<T>().completeOnTimeout(obj, ms, TimeUnit.MILLISECONDS)

    private object TimeoutObject
}
