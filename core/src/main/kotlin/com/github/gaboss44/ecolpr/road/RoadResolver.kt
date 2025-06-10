package com.github.gaboss44.ecolpr.road

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.util.OrderedList
import com.github.gaboss44.ecolpr.wrapper.LuckPermsRepository
import net.luckperms.api.cacheddata.CachedPermissionData
import net.luckperms.api.context.ContextSet
import org.bukkit.entity.Player

class RoadResolver(private val plugin: EcoLprPlugin) {

    fun getSatisfactoryRoads(player: Player): OrderedList<Road> {
        return RoadCategory.values()
            .asSequence()
            .filter { it.satisfies(player) }
            .sortedWith(roadComparator(plugin.repository, player))
            .toList()
            .let { OrderedList.from(it) }
    }

    fun getTravelingRoads(player: Player): OrderedList<Road> {
        val repository = plugin.repository
        val user = repository.getUser(player)

        val playerQueryOptions = user.queryOptions
        val playerContextSet = playerQueryOptions.context()

        return RoadCategory.values()
            .asSequence()
            .filter { it.isTraveledBy(player) }
            .sortedWith(roadComparator(repository, playerContextSet))
            .toList()
            .let { OrderedList.from(it) }
    }

    private fun Road.isApplicable(permissionData: CachedPermissionData, contextSet: ContextSet): Boolean =
        permissionData.checkPermission("group.$idOverride").asBoolean() &&
                queryOptions.satisfies(contextSet)

    private fun roadComparator(repository: LuckPermsRepository, player: Player): Comparator<Road> =
        roadComparator(repository, repository.getUser(player).queryOptions.context())

    private fun roadComparator(repository: LuckPermsRepository, contextSet: ContextSet): Comparator<Road> =
        compareByDescending<Road> { road ->
            repository.getGroup(road.idOverride)?.weight?.orElse(0) ?: 0
        }.thenByDescending { road ->
            calculateContextMatchScore(road.queryOptions.context(), contextSet)
        }

    private fun calculateContextMatchScore(roadContext: ContextSet, playerContext: ContextSet): Int =
        if (roadContext.isEmpty) 0 else roadContext.count { playerContext.contains(it) }

}