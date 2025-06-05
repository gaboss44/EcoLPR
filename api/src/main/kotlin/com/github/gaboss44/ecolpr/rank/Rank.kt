package com.github.gaboss44.ecolpr.rank

import com.willfp.eco.core.registry.KRegistrable
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player
import org.jetbrains.annotations.ApiStatus
import java.util.UUID

/**
 * Represents a rank within the plugin's ranking system.
 *
 * A [Rank] is held by any player that and may define custom behavior or visual display properties.
 * Each rank can optionally override the underlying LuckPerms group with [idOverride].
 *
 * This interface extends [KRegistrable] and is typically loaded and managed dynamically.
 */
@ApiStatus.NonExtendable
interface Rank : KRegistrable {

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
     * The display name of this rank or the closest inherited display name.
     * This value can be used as a fallback when [displayName] is `null`.
     * Returns `null` if [idOverride] is not related to any existing LuckPerms group.
     */
    val inheritedDisplayName: String?

    /**
     * Checks if this rank is currently held by the given [Player].
     *
     * @param player The online player to check.
     * @return `true` if the player currently holds this rank.
     */
    fun isHeldBy(player: Player): Boolean

    /**
     * Checks if this rank is held by the given [Player], using a specific [Road].
     *
     * @param player The online player to check.
     * @param road The [Road] configuration to apply during the lookup.
     * @return `true` if the player holds the rank under the given road.
     */
    fun isHeldBy(player: Player, road: Road): Boolean

    /**
     * Checks if this rank is held by a player using their [Player] instance and specific [QueryOptions].
     *
     * @param player The online player to check.
     * @param queryOptions The [QueryOptions] to use for the check.
     * @return `true` if the player holds the rank with the given query options.
     */
    fun isHeldBy(player: Player, queryOptions: QueryOptions): Boolean

    /**
     * Checks if this rank is held by a player using their [UUID].
     *
     * @param playerId The unique ID of the player.
     * @return `true` if the player holds the rank.
     */
    fun isHeldBy(playerId: UUID): Boolean

    /**
     * Checks if this rank is held by a player using their [UUID] and a specific [Road].
     *
     * @param playerId The unique ID of the player.
     * @param road The [Road] configuration to apply during the lookup.
     * @return `true` if the player holds the rank under the given road.
     */
    fun isHeldBy(playerId: UUID, road: Road): Boolean

    /**
     * Checks if this rank is held by a player using their [UUID] and specific [QueryOptions].
     *
     * @param playerId The unique ID of the player.
     * @param queryOptions The [QueryOptions] to use for the check.
     * @return `true` if the player holds the rank with the given query options.
     */
    fun isHeldBy(playerId: UUID, queryOptions: QueryOptions): Boolean
}