package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.road.Road
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionTravelingRoads : RoadCondition("traveling_roads") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: List<Road>
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val mode = EvaluationMode.of(config.getString("mode")) ?: EvaluationMode.ANY

        return when (mode) {
            EvaluationMode.ANY -> compileData.any { it.isTraveledBy(player) }
            EvaluationMode.ALL -> compileData.all { it.isTraveledBy(player) }
        }
    }
}