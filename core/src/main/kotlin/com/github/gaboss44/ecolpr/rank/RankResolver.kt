package com.github.gaboss44.ecolpr.rank

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.util.OrderedSet
import com.github.gaboss44.ecolpr.util.OrderedSetImpl
import com.github.gaboss44.ecolpr.wrapper.TrackWrapper
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID

/**
 * Class responsible for resolving and managing player ranks.
 *
 * This class provides methods to get, set, and manipulate player ranks
 * across different roads.
 */
class RankResolver(private val plugin: EcoLprPlugin) {
    
    /**
     * Gets the current rank of a player using their UUID.
     * 
     * This method finds which rank the player holds by checking through 
     * all registered ranks and testing if the player has permissions for it.
     * 
     * @param playerId The unique ID of the player to check
     * @return The current rank of the player, or null if no rank is found
     */
    fun getCurrentRank(playerId: UUID): Rank? {
        val user = plugin.repository.getUser(playerId) ?: return null

        val playerRanks = Ranks.values().filter { rank -> rank.isHeldBy(playerId) }

        if (playerRanks.isEmpty()) {
            return null
        }

        if (playerRanks.size > 1) {
            val primaryGroupName = user.primaryGroup

            val primaryRank = playerRanks.find { it.idOverride == primaryGroupName }
            if (primaryRank != null) { return primaryRank }

            return playerRanks.maxByOrNull { rank ->
                plugin.repository.getGroup(rank.idOverride)?.weight?.orElse(0) ?: 0
            }
        }

        return playerRanks.first()
    }

    /**
     * Gets the current rank of a player for a specific road.
     *
     * This method finds which rank the player holds in the given road by checking
     * the ordered ranks in the specified road and finding the highest one held by the player.
     *
     * @param playerId The unique ID of the player to check
     * @param road The road to check ranks for
     * @return The current rank of the player in the specified road, or null if no rank is found
     */
    fun getCurrentRank(playerId: UUID, road: Road): Rank? {
        plugin.repository.getUser(playerId) ?: return null

        val roadRanks = getOrderedRanks(road).also { if (it.isEmpty()) return null }

        val playerRoadRanks = roadRanks.filter { rank -> rank.isHeldBy(playerId, road) }

        if (playerRoadRanks.isEmpty()) return null

        return if (playerRoadRanks.size > 1) {
            playerRoadRanks.maxByOrNull { rank -> roadRanks.getPosition(rank) }
        } else {
            playerRoadRanks.first()
        }
    }

    /**
     * Gets the current rank of an online player.
     *
     * Convenience method that calls [getCurrentRank] with the player's UUID.
     *
     * @param player The online player to check
     * @return The current rank of the player, or null if no rank is found
     */
    fun getCurrentRank(player: Player): Rank? {
        return getCurrentRank(player.uniqueId)
    }

    /**
     * Gets the current rank of an online player for a specific road.
     *
     * Convenience method that calls [getCurrentRank] with the player's UUID and the specified road.
     *
     * @param player The online player to check
     * @param road The road to check ranks for
     * @return The current rank of the player in the specified road, or null if no rank is found
     */
    fun getCurrentRank(player: Player, road: Road): Rank? {
        return getCurrentRank(player.uniqueId, road)
    }

    /**
     * Gets the ordered ranks for a specific road.
     * 
     * This method retrieves the track associated with the road and orders the ranks
     * according to the order defined in the track in real-time.
     * 
     * @param road The road for which to get the ordered ranks
     * @return An ordered set of ranks for the specified road
     */
    fun getOrderedRanks(road: Road): OrderedSet<Rank> {
        val track = plugin.repository.getTrack(road.idOverride) ?: return OrderedSetImpl.empty()
        return orderRanksByTrack(track, road)
    }

    /**
     * Gets the next rank in a road after the current rank.
     *
     * @param currentRank The current rank
     * @param road The road in which to look for the next rank
     * @return The next rank, or null if the current rank is the last in the road
     */
    fun getNextRank(currentRank: Rank, road: Road): Rank? {
        val orderedRanks = getOrderedRanks(road).toList()
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
        val orderedRanks = getOrderedRanks(road).toList()
        val currentIndex = orderedRanks.indexOf(currentRank)

        return if (currentIndex > 0) {
            orderedRanks[currentIndex - 1]
        } else null
    }

    /**
     * Sets a specific rank for a player in a road.
     *
     * @param player The player to set the rank for
     * @param rank The rank to set
     * @param road The road in which to set the rank
     * @param source The source that initiates the rank transition
     * @return true if the rank was set successfully, false otherwise
     */
    fun setRank(player: Player, rank: Rank, road: Road, source: RankTransition.Source): Boolean {
        val user = plugin.repository.getUser(player.uniqueId) ?: return false
        
        // Get the current rank
        val currentRank = getCurrentRank(player, road)
        
        // If the player already has this rank, do nothing
        if (currentRank == rank) return true
        
        try {
            // Implement logic to remove the current rank and set the new one
            if (currentRank != null) {
                // Logic to remove the current rank
                // Specific implementation logic should be added here according to the plugin structure
            }
            
            // Logic to add the new rank
            // Specific implementation logic should be added here according to the plugin structure
            
            // Notify the plugin about the rank change (if needed)
            // plugin.eventManager?.fireRankChangeEvent(player, currentRank, rank, road, source)
            
            return true
        } catch (e: Exception) {
            plugin.logger.severe("Error setting rank ${rank.id} for ${player.name}: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Overloaded setRank method for offline players.
     */
    fun setRank(player: OfflinePlayer, rank: Rank, road: Road, source: RankTransition.Source): Boolean {
        // Implement logic for offline players
        val uuid = player.uniqueId
        // The rest of the implementation would be similar to the online player version
        return false // Pending implementation
    }

    /**
     * Orders the ranks according to the track associated with the road.
     * 
     * @param track The track that defines the order of the ranks
     * @param road The road for which the ranks are being ordered
     * @return An ordered set of ranks
     */
    private fun orderRanksByTrack(track: TrackWrapper, road: Road): OrderedSet<Rank> {
        return track.groups
            .mapNotNull { group -> Ranks.values().find { rank -> rank.idOverride == group } }
            .let { OrderedSetImpl.from(it) }
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
        return orderedRanks.isNotEmpty() && orderedRanks.last() == rank
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