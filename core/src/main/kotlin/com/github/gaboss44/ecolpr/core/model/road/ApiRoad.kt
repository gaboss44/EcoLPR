package com.github.gaboss44.ecolpr.core.model.road

import org.bukkit.entity.Player

class ApiRoad(
    val handle: Road
) : com.github.gaboss44.ecolpr.api.model.road.Road by handle {

    override val ranks get() = this.handle.ranks.map { it.proxy }

    override fun getRanks(player: Player) = this.handle.getRanks(player).map { it.proxy }
}
