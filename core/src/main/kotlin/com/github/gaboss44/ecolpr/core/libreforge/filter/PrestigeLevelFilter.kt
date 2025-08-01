package com.github.gaboss44.ecolpr.core.libreforge.filter

import com.github.gaboss44.ecolpr.api.event.transition.TransitionEvent
import com.github.gaboss44.ecolpr.core.util.asPrestigeOrNull
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData

abstract class PrestigeLevelFilter(id: String) : Filter<NoCompileData, Int>(id) {
    final override fun getValue(config: Config, data: TriggerData?, key: String): Int {
        return config.getIntFromExpression(key, data)
    }

    final override fun isMet(data: TriggerData, value: Int, compileData: NoCompileData): Boolean {
        val level = (data.event as? TransitionEvent)?.transition?.asPrestigeOrNull()?.prestigeLevel ?: return false
        return compare(level, value)
    }

    abstract fun compare(level: Int, value: Int): Boolean
}