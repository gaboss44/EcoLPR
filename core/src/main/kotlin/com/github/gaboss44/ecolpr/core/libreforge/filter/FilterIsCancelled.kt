package com.github.gaboss44.ecolpr.core.libreforge.filter

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.Cancellable

object FilterIsCancelled : Filter<NoCompileData, Boolean>("ecolpr_is_cancelled") {
    override fun getValue(
        config: Config,
        data: TriggerData?,
        key: String
    ) = config.getBool(key)

    override fun isMet(
        data: TriggerData,
        value: Boolean,
        compileData: NoCompileData
    ): Boolean {
        val event = data.event as? Cancellable ?: return false
        return event.isCancelled == value
    }
}