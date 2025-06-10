package com.github.gaboss44.ecolpr.event

import com.github.gaboss44.ecolpr.transition.RankReboot
import org.bukkit.event.HandlerList

class PlayerRankRebootEvent(
    reboot: RankReboot
) : PlayerRankTransitionEvent(reboot.player, reboot) {

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