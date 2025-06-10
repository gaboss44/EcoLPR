package com.github.gaboss44.ecolpr.libreforge.effect

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.Cancellable

object EffectSetCancelled : Effect<NoCompileData>("set_cancelled") {
    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require("cancellation", "You must specify a cancellation state for the event.")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? Cancellable ?: return false
        event.isCancelled = config.getBool("cancellation")
        return true
    }
}