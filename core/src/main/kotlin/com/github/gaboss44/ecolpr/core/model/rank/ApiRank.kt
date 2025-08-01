package com.github.gaboss44.ecolpr.core.model.rank

import com.github.gaboss44.ecolpr.api.model.road.Road
import com.github.gaboss44.ecolpr.core.util.cast
import org.bukkit.entity.Player

class ApiRank(
    val handle: Rank
) : com.github.gaboss44.ecolpr.api.model.rank.Rank by handle {

    override fun isHeldBy(player: Player, road: Road): Boolean {
        return this.handle.isHeldBy(player, road.cast())
    }
}
