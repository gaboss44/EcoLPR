package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.road.RoadCategory
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionIsTravelingSomeRoad : Condition<NoCompileData>("is_traveling_some_road") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return RoadCategory.values().any { road -> road.isTraveledBy(player) }
    }
}