package com.github.gaboss44.ecolpr.event

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.RankTransition
import com.github.gaboss44.ecolpr.rank.Road
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerPostRankTransitionEvent(
    player: Player,
    override val road: Road,
    override val from: Rank,
    override val to: Rank,
    override val direction: RankTransition.Direction,
    override val flow: RankTransition.Flow,
    override val source: RankTransition.Source,
    override val status: RankTransition.Status,
    override val sender: Player? = player
): PlayerEvent(player), RankTransitionEvent, CommandTriggeredEvent {
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