package com.github.gaboss44.ecolpr.core.libreforge.filter

import com.github.gaboss44.ecolpr.api.event.transition.TransitionEvent
import com.github.gaboss44.ecolpr.api.transition.Transition
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterTransitionMode : Filter<Collection<Transition.Mode>, Collection<String>>("ecolpr_transition_mode") {
    override fun getValue(
        config: Config,
        data: TriggerData?,
        key: String
    ) = config.getStrings(key)

    override fun makeCompileData(
        config: Config,
        context: ViolationContext,
        values: Collection<String>
    ) = values.mapNotNull { value ->
        val mode = Transition.Mode[value]
        if (mode == null) {
            context.log(
                ConfigWarning(
                    id,
                    "Filter $id does not recognize setMode '$value'"
                )
            )
        }
        mode
    }


    override fun isMet(
        data: TriggerData,
        value: Collection<String>,
        compileData: Collection<Transition.Mode>
    ): Boolean {
        val mode = (data.event as? TransitionEvent)?.transition?.mode ?: return false
        return compileData.any { mode == it }
    }
}
