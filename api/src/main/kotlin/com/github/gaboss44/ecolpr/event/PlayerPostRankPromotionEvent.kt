package com.github.gaboss44.ecolpr.event

import com.github.gaboss44.ecolpr.transition.RankPromotion
import org.bukkit.event.HandlerList

class PlayerPostRankPromotionEvent(
    promotion: RankPromotion
) : PlayerPostRankTransitionEvent(promotion.player, promotion) {

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