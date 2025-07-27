package com.github.gaboss44.ecolpr.core.libreforge.effect

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.model.road.Roads
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

abstract class PrestigeWithTargetEffect(
    plugin: EcoLprPlugin,
    id: String
) : TransitionEffect(plugin, id) {
    override fun onTrigger(config: Config, data: TriggerData, compileData: Transition.Options): Boolean {
        val road = Roads[config.getStringOrNull("road")] ?: return false
        val player = data.player ?: return false
        val prestigeRoad = Roads[config.getStringOrNull("prestige-road")]
        return try {
            if (prestigeRoad == null) tryTransition(
                player,
                road,
                Transition.Source.SCRIPT,
                compileData
            )
            else tryTransition(
                player,
                road,
                prestigeRoad,
                Transition.Source.SCRIPT,
                compileData
            )
        } catch (_: RuntimeException) { false }
    }

    abstract fun tryTransition(
        player: Player,
        road: Road,
        prestigeRoad: Road,
        source: Transition.Source,
        options: Transition.Options
    ): Boolean
}