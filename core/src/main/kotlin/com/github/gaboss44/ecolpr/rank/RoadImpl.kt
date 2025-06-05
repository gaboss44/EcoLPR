package com.github.gaboss44.ecolpr.rank

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.util.OrderedList
import com.github.gaboss44.ecolpr.util.OrderedListImpl
import com.github.gaboss44.ecolpr.util.queryOptions
import com.github.gaboss44.ecolpr.wrapper.TrackWrapper
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.ViolationContext
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

    private val underlyingTrack: TrackWrapper? get() = plugin.repository.getTrack(idOverride)
    
    override val ranks: OrderedList<Rank> get() {
        val trackGroups = underlyingTrack?.groups ?: return OrderedListImpl.empty()
        val mappedRanks = trackGroups.mapNotNull { groupName -> 
            Ranks.values().find { rank -> rank.idOverride == groupName }
        }
        return OrderedListImpl.from(mappedRanks)
    }

    override fun satisfies(player: Player): Boolean =
        queryOptions.satisfies(plugin.repository.getUser(player).context)

    val testRankUpEffects = Effects.compileChain(
        config.getSubsections("test-rank-up-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "Rank $id test-rank-up-effects")
    )

    val postRankUpEffects = Effects.compileChain(
        config.getSubsections("post-rank-up-effects"),
        NormalExecutorFactory.create(),
        ViolationContext(plugin, "Rank $id post-rank-up-effects")
    )
}