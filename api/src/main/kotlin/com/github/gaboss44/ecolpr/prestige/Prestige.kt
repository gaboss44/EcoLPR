package com.github.gaboss44.ecolpr.prestige

import com.github.gaboss44.ecolpr.road.Road
import com.willfp.eco.core.registry.KRegistrable
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player

interface Prestige : KRegistrable {

    /**
     * The ID that overrides the actual LuckPerms (prestige) track ID.
     * This is used to link the prestige logic to a specific track in LuckPerms.
     */
    val idOverride: String

    val displayName: String?

    val groups: List<String>?

    val maxLevel: Int get() = groups?.size ?: -1

    fun getLevel(player: Player, road: Road): Int =
        getLevel(player, road.queryOptions)

    fun getLevel(player: Player, queryOptions: QueryOptions): Int
}