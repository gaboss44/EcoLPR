package com.github.gaboss44.ecolpr.libreforge.filter

import com.github.gaboss44.ecolpr.event.PlayerRankTransitionEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterToNoRank : Filter<NoCompileData, Boolean>("to_no_rank") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean =
        config.getBool(key)

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val absent = (data.event as? PlayerRankTransitionEvent)?.transition?.to == null
        return value == absent
    }
}