package com.github.gaboss44.ecolpr.core.libreforge.filter

import com.github.gaboss44.ecolpr.api.event.transition.TransitionEvent
import com.github.gaboss44.ecolpr.api.transition.Transition
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.triggers.TriggerData

object FilterTransitionSource : Filter<Collection<Transition.Source>, Collection<String>>("ecolpr_transition_source") {
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
        val source = Transition.Source.getByLowerValue(value)
        if (source == null) {
            context.log(
                ConfigWarning(
                    id,
                    "Filter $id does not recognize '$value'"
                )
            )
        }
        source
    }

    override fun isMet(
        data: TriggerData,
        value: Collection<String>,
        compileData: Collection<Transition.Source>
    ): Boolean {
        val source = (data.event as? TransitionEvent)?.transition?.source ?: return false
        return compileData.any { source.inherits(it) }
    }
}
