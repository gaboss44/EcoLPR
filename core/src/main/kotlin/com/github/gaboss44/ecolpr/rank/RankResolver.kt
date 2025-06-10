package com.github.gaboss44.ecolpr.rank

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.road.Road
import com.github.gaboss44.ecolpr.util.OrderedList
import com.github.gaboss44.ecolpr.wrapper.TrackWrapper

/**
 * Class responsible for resolving and managing player ranks.
 *
 * This class provides methods to get, set, and manipulate player ranks
 * across different roads.
 */
class RankResolver(private val plugin: EcoLprPlugin) {

    /**
     * Gets the ordered ranks for a specific road.
     * 
     * This method retrieves the track associated with the road and orders the ranks
     * according to the order defined in the track in real-time.
     * 
     * @param road The road for which to get the ordered ranks
     * @return An ordered set of ranks for the specified road
     */
    fun getOrderedRanks(road: Road): OrderedList<Rank> {
        val track = plugin.repository.getTrack(road.idOverride) ?: return OrderedList.empty()
        return orderRanksByTrack(track)
    }

    /**
     * Gets the next rank in a road after the current rank.
     *
     * @param currentRank The current rank
     * @param road The road in which to look for the next rank
     * @return The next rank, or null if the current rank is the last in the road
     */
    fun getNextRank(currentRank: Rank, road: Road): Rank? {
        val orderedRanks = getOrderedRanks(road)
        val currentIndex = orderedRanks.indexOf(currentRank)

        return if (currentIndex != -1 && currentIndex < orderedRanks.size - 1) {
            orderedRanks[currentIndex + 1]
        } else null
    }

    /**
     * Gets the previous rank in a road before the current rank.
     *
     * @param currentRank The current rank
     * @param road The road in which to look for the previous rank
     * @return The previous rank, or null if the current rank is the first in the road
     */
    fun getPreviousRank(currentRank: Rank, road: Road): Rank? {
        val orderedRanks = getOrderedRanks(road)
        val currentIndex = orderedRanks.indexOf(currentRank)

        return if (currentIndex > 0) {
            orderedRanks[currentIndex - 1]
        } else null
    }

    /**
     * Orders the ranks according to the track associated with the road.
     * 
     * @param track The track that defines the order of the ranks
     * @return An ordered set of ranks
     */
    private fun orderRanksByTrack(track: TrackWrapper): OrderedList<Rank> {
        return track.groups
            .mapNotNull { group -> RankCategory.values().find { rank -> rank.idOverride == group } }
            .let { OrderedList.from(it) }
    }
    
    /**
     * Gets the position of a rank in a road.
     * 
     * @param rank The rank whose position is to be obtained
     * @param road The road in which to check the position
     * @return The position of the rank in the road, or -1 if the rank is not in the road
     */
    fun getRankPosition(rank: Rank, road: Road): Int {
        return getOrderedRanks(road).getPosition(rank)
    }
    
    /**
     * Checks if a rank is the last one in a road.
     * 
     * @param rank The rank to check
     * @param road The road in which to check
     * @return true if the rank is the last one in the road, false otherwise
     */
    fun isLastRank(rank: Rank, road: Road): Boolean {
        val orderedRanks = getOrderedRanks(road)
        return !orderedRanks.isEmpty() && orderedRanks.last() == rank
    }
    
    /**
     * Checks if a rank is the first one in a road.
     * 
     * @param rank The rank to check
     * @param road The road in which to check
     * @return true if the rank is the first one in the road, false otherwise
     */
    fun isFirstRank(rank: Rank, road: Road): Boolean {
        val orderedRanks = getOrderedRanks(road)
        return orderedRanks.isNotEmpty() && orderedRanks.first() == rank
    }
}