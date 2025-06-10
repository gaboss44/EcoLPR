package com.github.gaboss44.ecolpr.event

import com.github.gaboss44.ecolpr.transition.RankGraduation
import org.bukkit.event.HandlerList

class PlayerRankGraduationEvent(
    graduation: RankGraduation
) : PlayerRankTransitionEvent(graduation.player, graduation) {

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