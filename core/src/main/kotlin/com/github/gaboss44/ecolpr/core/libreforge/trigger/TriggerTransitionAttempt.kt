package com.github.gaboss44.ecolpr.core.libreforge.trigger

import com.github.gaboss44.ecolpr.api.event.transition.TransitionAttemptEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerTransitionAttempt : Trigger("ecolpr_transition_attempt") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler
    fun handle(event: TransitionAttemptEvent) {
        val player = event.transition.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event
            )
        )
    }
}
