package com.github.gaboss44.ecolpr.rank

import com.github.gaboss44.ecolpr.util.OrderedList
import com.willfp.eco.core.registry.KRegistrable
import net.luckperms.api.context.ContextSet
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Road : KRegistrable {
    /**
     * The ID that overrides the actual LuckPerms group ID.
     * This is used to link the rank logic to a specific group in LuckPerms.
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
    /**
     * The list of [Rank]s that are part of this road.
     * This is an ordered list, meaning the ranks have a specific order of precedence.
     */
    val ranks: OrderedList<Rank>

    /**
     * Checks if this road contains a specific [Rank].
     */
    val contains: (Rank) -> Boolean get() = { rank -> ranks.contains(rank) }

    fun satisfies(player: Player): Boolean

    fun satisfies(context: ContextSet): Boolean =
        queryOptions.satisfies(context)

    fun contains(player: Player): Boolean =
        this.satisfies(player) && ranks.any { it.isHeldBy(player) }

    fun getRank(player: Player): Rank? =
        ranks.firstOrNull { it.isHeldBy(player) }
}