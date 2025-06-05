package com.github.gaboss44.ecolpr.event

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.RankTransition
import com.github.gaboss44.ecolpr.rank.Road
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.command.CommandSender

class PlayerRankTransitionEvent(
    who: Player,
    override val sender: CommandSender = who,
    private val transition: RankTransition
): PlayerEvent(who), RankTransitionEvent, CommandTriggeredEvent, Cancellable {
    override val road: Road get() = transition.road
    override val from: Rank? get() = transition.from
    override val to: Rank? get() = transition.to
    override val direction: RankTransition.Direction get() = transition.direction
    override val flow: RankTransition.Flow get() = transition.flow
    override val source: RankTransition.Source get() = transition.source
    override val status: RankTransition.Status get() = transition.status

    private var cancelled = false

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

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