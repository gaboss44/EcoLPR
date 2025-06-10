package com.github.gaboss44.ecolpr.libreforge.filter

import com.github.gaboss44.ecolpr.event.PlayerRankTransitionEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterRankTransitionSource : Filter<NoCompileData, Collection<String>>("rank_transition_source") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val source = (data.event as? PlayerRankTransitionEvent)?.transition?.source ?: return false
        return value.containsIgnoreCase(source.lowerValue)
    }
}