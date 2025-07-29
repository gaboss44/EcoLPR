package com.github.gaboss44.ecolpr.core.libreforge.filter

import com.github.gaboss44.ecolpr.api.event.transition.TransitionEvent
import com.github.gaboss44.ecolpr.core.util.asToRankOrNull
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterTransitionToRank : Filter<NoCompileData, Collection<String>>("ecolpr_transition_to_rank") {
    override fun getValue(
        config: Config,
        data: TriggerData?,
        key: String
    ) = config.getStrings(key)

    override fun isMet(
        data: TriggerData,
        value: Collection<String>,
        compileData: NoCompileData
    ) = (data.event as? TransitionEvent)
        ?. transition
        ?. asToRankOrNull()
        ?. toRank
        ?. let { value.containsIgnoreCase(it.name) }
        ?: false
}