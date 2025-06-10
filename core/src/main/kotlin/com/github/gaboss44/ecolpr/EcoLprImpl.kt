package com.github.gaboss44.ecolpr

import com.github.gaboss44.ecolpr.prestige.Prestige
import com.github.gaboss44.ecolpr.prestige.PrestigeCategory
import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.RankCategory
import com.github.gaboss44.ecolpr.road.Road
import com.github.gaboss44.ecolpr.road.RoadCategory
import com.github.gaboss44.ecolpr.transition.RankTransitioner
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player

class EcoLprImpl(private val plugin: EcoLprPlugin) : EcoLpr {
    override val defaultQueryOptions: QueryOptions get() = plugin.defaultQueryOptions

    override val rankTransitioner: RankTransitioner get() = plugin.rankTransitioner

    override fun getRankById(id: String): Rank? = RankCategory.getByID(id)

    override fun getRankByIdOverride(id: String): Rank? = RankCategory.getByIdOverride(id)

    override fun getRoadById(id: String): Road? = RoadCategory.getByID(id)

    override fun getRoadByIdOverride(id: String): Road? = RoadCategory.getByIdOverride(id)

    override fun getPrestigeById(id: String): Prestige? = PrestigeCategory.getByID(id)

    override fun getPrestigeByIdOverride(id: String): Prestige? = PrestigeCategory.getByIdOverride(id)

    override fun getSatisfactoryRoads(player: Player): List<Road> =
        plugin.roadResolver.getSatisfactoryRoads(player)
}