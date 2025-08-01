package com.github.gaboss44.ecolpr.core.libreforge.effect

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.Cancellable

object EffectSetCancelled : Effect<NoCompileData>("ecolpr_set_cancelled") {
    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val supportsDelay = false

    override val arguments = arguments {
        require("value", "You must specify a cancellation state (true or false).")
    }

    override fun onTrigger(
        config: Config,
        data: TriggerData,
        compileData: NoCompileData
    ): Boolean {
        val value = config.getBoolOrNull("value") ?: return false
        val event = data.event as? Cancellable ?: return false
        event.isCancelled = value
        return true
    }
}