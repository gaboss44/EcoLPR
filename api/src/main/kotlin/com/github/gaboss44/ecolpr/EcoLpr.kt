package com.github.gaboss44.ecolpr

import com.github.gaboss44.ecolpr.prestige.Prestige
import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.road.Road
import com.github.gaboss44.ecolpr.transition.RankTransitioner
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player

interface EcoLpr {
    val defaultQueryOptions: QueryOptions

    val rankTransitioner: RankTransitioner

    fun getRankById(id: String): Rank?

    fun getRankByIdOverride(id: String): Rank?

    fun getRoadById(id: String): Road?

    fun getRoadByIdOverride(id: String): Road?

    fun getPrestigeById(id: String): Prestige?

    fun getPrestigeByIdOverride(id: String): Prestige?

    fun getSatisfactoryRoads(player: Player): List<Road>
}