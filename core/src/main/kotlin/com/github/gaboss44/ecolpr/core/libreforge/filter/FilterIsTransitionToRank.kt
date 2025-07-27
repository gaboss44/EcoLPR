package com.github.gaboss44.ecolpr.core.libreforge.filter

import com.github.gaboss44.ecolpr.api.event.transition.TransitionEvent
import com.github.gaboss44.ecolpr.core.util.isToRank
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterIsTransitionToRank : Filter<NoCompileData, Boolean>("ecolpr_is_transition_to_rank") {
    override fun getValue(
        config: Config,
        data: TriggerData?,
        key: String
    ) = config.getBool(key)

    override fun isMet(
        data: TriggerData,
        value: Boolean,
        compileData: NoCompileData
    ) = (data.event as? TransitionEvent)
        ?. transition
        ?. isToRank()
        ?: false
}