package com.github.gaboss44.ecolpr.event

import com.github.gaboss44.ecolpr.transition.RankDemotion
import org.bukkit.event.HandlerList

class PlayerPostRankDemotionEvent(
    demotion: RankDemotion
) : PlayerPostRankTransitionEvent(demotion.player, demotion) {

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