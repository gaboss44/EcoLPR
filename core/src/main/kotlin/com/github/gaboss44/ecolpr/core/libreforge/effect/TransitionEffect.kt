package com.github.gaboss44.ecolpr.core.libreforge.effect

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.model.road.Roads
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

abstract class TransitionEffect(
    protected val plugin: EcoLprPlugin,
    id: String
) : Effect<Transition.Options>(id) {
    final override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    final override val arguments = arguments {
        require("road", "You must specify a road")
    }

    final override fun makeCompileData(
        config: Config,
        context: ViolationContext
    ) = Transition.Options.parseArgs(config)

    final override fun onTrigger(config: Config, data: TriggerData, compileData: Transition.Options): Boolean {
        val player = data.player ?: return false
        val road = Roads[config.getStringOrNull("road")] ?: return false
        return try {
            executeTransition(
                player,
                road,
                Transition.Source.SCRIPT,
                compileData
            )
        } catch (_: Exception) { false }
    }

    abstract fun executeTransition(
        player: Player,
        road: Road,
        source: Transition.Source,
        options: Transition.Options
    ): Boolean
}