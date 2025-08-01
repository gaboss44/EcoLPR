package com.github.gaboss44.ecolpr.core.libreforge.trigger

import com.github.gaboss44.ecolpr.api.event.transition.TransitionResultEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerTransitionResult : Trigger("ecolpr_transition_result") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler
    fun handle(event: TransitionResultEvent) {
        val player = event.transition.player

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