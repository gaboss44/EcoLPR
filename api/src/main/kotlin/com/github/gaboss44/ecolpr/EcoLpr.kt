package com.github.gaboss44.ecolpr

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.Road
import net.luckperms.api.query.QueryOptions
import java.util.UUID

interface EcoLpr {
    val defaultQueryOptions: QueryOptions

    val shouldDebug: Boolean

    val shouldSelectMostSpecificRoad: Boolean

    fun getRankById(id: String): Rank?

    fun getRankByIdOverride(id: String): Rank?

    fun getRoadById(id: String): Road?

    fun getRoadByIdOverride(id: String): Road?

    fun searchPlayerCurrentRoads(playerId: UUID): Set<Road>

    fun searchPlayerMostSpecificRoad(playerId: UUID): Road? =
        searchPlayerCurrentRoads(playerId).firstOrNull()

    fun searchPlayerLeastSpecificRoad(playerId: UUID): Road? =
        searchPlayerCurrentRoads(playerId).lastOrNull()
}