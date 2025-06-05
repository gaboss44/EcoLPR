package com.github.gaboss44.ecolpr

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.Ranks
import com.github.gaboss44.ecolpr.rank.Road
import com.github.gaboss44.ecolpr.rank.Roads
import net.luckperms.api.query.QueryOptions
import java.util.UUID

class EcoLprImpl(private val plugin: EcoLprPlugin) : EcoLpr {
    override val defaultQueryOptions: QueryOptions get() = plugin.defaultQueryOptions

    override val shouldDebug: Boolean = plugin.shouldDebug

    override val shouldSelectMostSpecificRoad: Boolean = plugin.shouldSelectMostSpecificRoad

    override fun getRankById(id: String): Rank? = Ranks.getByID(id)

    override fun getRankByIdOverride(id: String): Rank? = Ranks.getByIdOverride(id)

    override fun getRoadById(id: String): Road? = Roads.getByID(id)

    override fun getRoadByIdOverride(id: String): Road? = Roads.getByIdOverride(id)

    override fun searchPlayerCurrentRoads(playerId: UUID): Set<Road> =
        plugin.roadResolver.getPlayerRoads(playerId)
}