package com.github.gaboss44.ecolpr.core.libreforge.filter

import com.github.gaboss44.ecolpr.api.event.transition.TransitionResultEvent
import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.util.asResultOrNull
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterTransitionStatus : Filter<Collection<Transition.Status>, Collection<String>>("ecolpr_transition_status") {
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
        val status = Transition.Status[value]
        if (status == null) {
            context.log(
                ConfigWarning(
                    id,
                    "Filter $id does not recognize status '$value'"
                )
            )
        }
        status
    }


    override fun isMet(
        data: TriggerData,
        value: Collection<String>,
        compileData: Collection<Transition.Status>
    ): Boolean {
        val status = (data.event as? TransitionResultEvent)?.transition?.asResultOrNull()?.status ?: return false
        return compileData.any { status.inherits(it) }
    }
}
