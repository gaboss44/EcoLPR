package com.github.gaboss44.ecolpr.event

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.road.Road
import com.github.gaboss44.ecolpr.transition.RankTransition
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

abstract class PlayerPostRankTransitionEvent(
    who: Player,
    val transition: RankTransition
): PlayerEvent(who) {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}