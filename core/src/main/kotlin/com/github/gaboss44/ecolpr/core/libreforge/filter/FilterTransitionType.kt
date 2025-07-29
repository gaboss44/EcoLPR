package com.github.gaboss44.ecolpr.core.libreforge.filter

import com.github.gaboss44.ecolpr.api.event.transition.TransitionEvent
import com.github.gaboss44.ecolpr.api.transition.Transition
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.triggers.TriggerData

object FilterTransitionType : Filter<Collection<Transition.Type>, Collection<String>>("ecolpr_transition_type") {
    override fun getValue(
        config: Config,
        data: TriggerData?,
        key: String
    ) = config.getStrings(key, key)

    override fun makeCompileData(
        config: Config,
        context: ViolationContext,
        values: Collection<String>
    ) = values.mapNotNull { value ->
        val type = Transition.Type[value]
        if (type == null) {
            context.log(
                ConfigWarning(
                    id,
                    "Filter $id does not recognize '$value'"
                )
            )
        }
        type
    }

    override fun isMet(
        data: TriggerData,
        value: Collection<String>,
        compileData: Collection<Transition.Type>
    ): Boolean {
        val type = (data.event as? TransitionEvent)?.transition?.type ?: return false
        return compileData.any { type.inherits(it) }
    }
}
