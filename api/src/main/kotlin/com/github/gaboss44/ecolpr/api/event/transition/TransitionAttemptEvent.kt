package com.github.gaboss44.ecolpr.api.event.transition

import com.github.gaboss44.ecolpr.api.EcoLpr
import com.github.gaboss44.ecolpr.api.transition.Transition
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
class TransitionAttemptEvent (
    override val ecoLpr: EcoLpr,
    override val transition: Transition.Attempt
) : PlayerEvent(transition.player), TransitionEvent, Cancellable {

    private var cancelled = false

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) { cancelled = cancel }

    override fun getHandlers() = handlerList

    companion object {

        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlerList
    }
}