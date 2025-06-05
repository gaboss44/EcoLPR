package com.github.gaboss44.ecolpr.rank

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.util.OrderedList
import com.github.gaboss44.ecolpr.util.OrderedListImpl
import com.github.gaboss44.ecolpr.wrapper.LuckPermsRepository
import net.luckperms.api.cacheddata.CachedPermissionData
import net.luckperms.api.context.ContextSet
import org.bukkit.entity.Player
import java.util.UUID

class RoadResolver(private val plugin: EcoLprPlugin) {

    /**
     * Gets all roads available to a player as an OrderedSet
     * 
     * @param playerId The unique ID of the player to check
     * @return An ordered list of roads the player has access to
     */
    fun getPlayerRoads(playerId: UUID): OrderedList<Road> {
        val repository = plugin.repository
        val user = repository.getUser(playerId) ?: return OrderedListImpl.empty()

        val playerQueryOptions = user.queryOptions
        val playerContextSet = playerQueryOptions.context()
        val permissionData = user.permissionData

        val orderedRoads = Roads.values()
            .asSequence()
            .filter { it.isApplicable(permissionData, playerContextSet) }
            .sortedWith(roadComparator(repository, playerContextSet))
            .toList()
            
        return OrderedListImpl.from(orderedRoads)
    }
    
    /**
     * Gets all roads available to an online player as an OrderedSet
     * 
     * @param player The online player to check
     * @return An ordered list of roads the player has access to
     */
    fun getPlayerRoads(player: Player): OrderedList<Road> {
        val repository = plugin.repository
        val user = repository.getUser(player)

        val playerQueryOptions = user.queryOptions
        val playerContextSet = playerQueryOptions.context()
        val permissionData = user.permissionData

        val orderedRoads = Roads.values()
            .asSequence()
            .filter { it.isApplicable(permissionData, playerContextSet) }
            .sortedWith(roadComparator(repository, playerContextSet))
            .toList()

        return OrderedListImpl.from(orderedRoads)
    }
    
    /**
     * Gets the next road in the player's progression
     * 
     * @param playerId The unique ID of the player
     * @param currentRoad The current road
     * @return The next road, or null if the current road is the last one
     */
    fun getNextRoad(playerId: UUID, currentRoad: Road): Road? {
        return getPlayerRoads(playerId).getNext(currentRoad)
    }
    
    /**
     * Gets the previous road in the player's progression
     * 
     * @param playerId The unique ID of the player
     * @param currentRoad The current road
     * @return The previous road, or null if the current road is the first one
     */
    fun getPreviousRoad(playerId: UUID, currentRoad: Road): Road? {
        return getPlayerRoads(playerId).getPrevious(currentRoad)
    }
    
    /**
     * Gets the position of a road in the player's progression
     * 
     * @param playerId The unique ID of the player
     * @param road The road to check
     * @return The position of the road, or -1 if the player doesn't have access to the road
     */
    fun getRoadPosition(playerId: UUID, road: Road): Int {
        return getPlayerRoads(playerId).getPosition(road)
    }
    
    /**
     * Checks if a road is the last one in the player's progression
     * 
     * @param playerId The unique ID of the player
     * @param road The road to check
     * @return true if the road is the last one, false otherwise
     */
    fun isLastRoad(playerId: UUID, road: Road): Boolean {
        val roads = getPlayerRoads(playerId)
        return roads.isNotEmpty() && roads.isLast(road)
    }
    
    /**
     * Checks if a road is the first one in the player's progression
     * 
     * @param playerId The unique ID of the player
     * @param road The road to check
     * @return true if the road is the first one, false otherwise
     */
    fun isFirstRoad(playerId: UUID, road: Road): Boolean {
        val roads = getPlayerRoads(playerId)
        return roads.isNotEmpty() && roads.isFirst(road)
    }

    private fun Road.isApplicable(permissionData: CachedPermissionData, contextSet: ContextSet): Boolean =
        permissionData.checkPermission("group.$idOverride").asBoolean() &&
                queryOptions.satisfies(contextSet)

    private fun roadComparator(repository: LuckPermsRepository, contextSet: ContextSet): Comparator<Road> =
        compareByDescending<Road> { road ->
            repository.getGroup(road.idOverride)?.weight?.orElse(0) ?: 0
        }.thenByDescending { road ->
            calculateContextMatchScore(road.queryOptions.context(), contextSet)
        }

    private fun calculateContextMatchScore(roadContext: ContextSet, playerContext: ContextSet): Int =
        if (roadContext.isEmpty) 0 else roadContext.count { playerContext.contains(it) }

}
