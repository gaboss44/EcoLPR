package com.github.gaboss44.ecolpr.core.libreforge.effect

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.model.road.Road
import org.bukkit.entity.Player

class EffectPrestige(plugin: EcoLprPlugin) : TransitionEffect(plugin, "ecolpr_prestige") {
    override fun executeTransition(
        player: Player,
        road: Road,
        source: Transition.Source,
        options: Transition.Options
    ) = plugin.transitionManager.prestige(
        player,
        road,
        source,
        options
    ).wasSuccessful()
}