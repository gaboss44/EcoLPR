package com.github.gaboss44.ecolpr.core.libreforge.condition

import com.github.gaboss44.ecolpr.core.model.road.Roads
import com.github.gaboss44.ecolpr.core.util.MatchMode
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getStrings
import org.bukkit.entity.Player

object ConditionOnRoad : Condition<NoCompileData>("ecolpr_on_road") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val roads = config.getStrings("roads", "road").mapNotNull { Roads[it] }
        val mode = MatchMode[config.getStringOrNull("match-mode")] ?: MatchMode.ANY
        return mode.evaluate(roads) { it.getRanks(player).isNotEmpty() }
    }
}