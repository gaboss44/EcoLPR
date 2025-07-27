package com.github.gaboss44.ecolpr.api.util

import com.github.gaboss44.ecolpr.api.model.road.Road
import org.bukkit.entity.Player

interface RankMap {

    val road: Road

    val player: Player
}