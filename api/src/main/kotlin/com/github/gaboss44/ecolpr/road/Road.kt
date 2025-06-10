package com.github.gaboss44.ecolpr.road

import com.github.gaboss44.ecolpr.prestige.Prestige
import com.github.gaboss44.ecolpr.rank.Rank
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.Chain
import net.luckperms.api.context.ContextSet
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Road : KRegistrable {

    /**
     * The ID that overrides the actual LuckPerms track ID.
     * This is used to link the rank logic to a specific track in LuckPerms.
     */
    val idOverride: String

    /**
     * The display name of this rank, as defined in its configuration.
     * May include color codes and formatting.
     */
    val displayName: String?

    /**
     * The query options used for this road.
     * This is typically used to apply specific context or settings when checking conditions.
     */
    val queryOptions: QueryOptions

    val genericConditions: ConditionList

    val promotionConditions: ConditionList

    val demotionConditions: ConditionList

    val graduationConditions: ConditionList

    val rebootConditions: ConditionList

    val rankTransitionEffects: Chain?

    val postRankTransitionEffects: Chain?

    val groups: List<String>?

    /**
     * The list of [Rank]s that are part of this road.
     */
    val ranks: List<Rank>

    val next: Road?

    val prestige: Prestige?

    /**
     * Checks if this road contains a specific [Rank].
     */
    val contains: (Rank) -> Boolean get() = { rank -> ranks.contains(rank) }

    /**
     * Checks if this road's query options satisfy the given [Player]'s context.
     *
     * @param player The online player to check against.
     * @return `true` if this road's query options satisfy the player's context.
     */
    fun satisfies(player: Player): Boolean

    /**
     * Checks if this road's query options satisfy the given [ContextSet].
     *
     * @param context The context set to check against.
     * @return `true` if this road's query options satisfy the context.
     */
    fun satisfies(context: ContextSet): Boolean =
        queryOptions.satisfies(context)

    fun isTraveledBy(player: Player): Boolean =
        this.satisfies(player) && ranks.any { it.isHeldBy(player) }

    fun getRank(player: Player): Rank? =
        ranks.firstOrNull { it.isHeldBy(player, this) }

    fun getSatisfactoryRoads(player: Player): List<Road>
}