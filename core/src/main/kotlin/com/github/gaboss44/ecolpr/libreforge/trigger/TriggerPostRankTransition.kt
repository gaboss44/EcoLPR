package com.github.gaboss44.ecolpr.libreforge.trigger

import com.github.gaboss44.ecolpr.event.PlayerPostRankTransitionEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerPostRankTransition : Trigger("post_rank_transition") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler
    fun handle(event: PlayerPostRankTransitionEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                value = event.transition.status.triggerValue
            )
        )
    }
}