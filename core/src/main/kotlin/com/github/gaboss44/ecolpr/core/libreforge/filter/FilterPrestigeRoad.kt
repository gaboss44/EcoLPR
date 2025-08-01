package com.github.gaboss44.ecolpr.core.libreforge.filter

import com.github.gaboss44.ecolpr.api.event.transition.TransitionEvent
import com.github.gaboss44.ecolpr.core.util.asPrestigeWithTargetOrNull
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterPrestigeRoad : Filter<NoCompileData, Collection<String>>("ecolpr_prestige_road") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val road = (data.event as? TransitionEvent)?.transition?.asPrestigeWithTargetOrNull()?.prestigeRoad ?: return false
        return value.any { it.equals(road.name, ignoreCase = true) }
    }
}