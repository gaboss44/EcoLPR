package com.github.gaboss44.ecolpr.road

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.prestige.PrestigeCategory
import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.RankCategory
import com.github.gaboss44.ecolpr.util.OrderedList
import com.github.gaboss44.ecolpr.util.queryOptions
import com.github.gaboss44.ecolpr.wrapper.TrackWrapper
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import org.bukkit.entity.Player

class RoadImpl(
    override val id: String,
    config: Config,
    private val plugin: EcoLprPlugin,
) : Road {
    init {
        PlayerPlaceholder(plugin, "${id}_id_override") { idOverride }.register()
        PlayerPlaceholder(plugin, "${id}_display_name") { displayName?.toNiceString() ?: "" }.register()
    }

    override val idOverride: String = config.getStringOrNull("id-override") ?: id

    override val displayName: String? = config.getStringOrNull("display-name")

    override val queryOptions = config.getSubsectionOrNull("query-options")?.queryOptions ?: plugin.defaultQueryOptions

    override val genericConditions = Conditions.compile(
        config.getSubsections("generic-conditions"),
        ViolationContext(plugin, "Road $id generic-conditions"),
    )

    override val promotionConditions = Conditions.compile(
        config.getSubsections("promotion-conditions"),
        ViolationContext(plugin, "Road $id promotion-conditions"),
    )

    override val demotionConditions = Conditions.compile(
        config.getSubsections("demotion-conditions"),
        ViolationContext(plugin, "Road $id demotion-conditions"),
    )

    override val graduationConditions = Conditions.compile(
        config.getSubsections("graduation-conditions"),
        ViolationContext(plugin, "Road $id graduation-conditions"),
    )

    override val rebootConditions = Conditions.compile(
        config.getSubsections("reboot-conditions"),
        ViolationContext(plugin, "Road $id reboot-conditions"),
    )

    override val rankTransitionEffects = Effects.compileChain(
        config.getSubsections("rank-transition-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "Road $id rank-transition-effects")
    )

    override val postRankTransitionEffects = Effects.compileChain(
        config.getSubsections("post-rank-transition-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "Road $id post-rank-transition-effects")
    )

    private val underlyingTrack: TrackWrapper? get() = plugin.repository.getTrack(idOverride)

    override val groups: List<String>? get() = underlyingTrack?.groups

    override val ranks: OrderedList<Rank>
        get() {
        val trackGroups = groups ?: return OrderedList.Companion.empty()
        val mappedRanks = trackGroups.mapNotNull { group -> RankCategory.values().find { rank -> rank.idOverride == group } }
        return OrderedList.Companion.from(mappedRanks)
    }

    val nextStr = config.getStringOrNull("graduation-next-road")?.lowercase()

    override val next get() = nextStr?.let { RoadCategory.values().find { road -> road.id == it }}

    val prestigeStr = config.getStringOrNull("graduation-next-prestige")?.lowercase()

    override val prestige get() = prestigeStr?.let { PrestigeCategory.values().find { prestige -> prestige.id == it} }

    override fun getSatisfactoryRoads(player: Player): List<Road> =
        plugin.roadResolver.getSatisfactoryRoads(player)

    override fun satisfies(player: Player): Boolean =
        queryOptions.satisfies(plugin.repository.getUser(player).context)
}