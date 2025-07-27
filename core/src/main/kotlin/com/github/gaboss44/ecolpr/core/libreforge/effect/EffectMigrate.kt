package com.github.gaboss44.ecolpr.core.libreforge.effect

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.model.road.Road
import org.bukkit.entity.Player

class EffectMigrate(plugin: EcoLprPlugin) : PrestigeWithTargetEffect(plugin, "ecolpr_migrate") {
    override fun tryTransition(
        player: Player,
        road: Road,
        source: Transition.Source,
        options: Transition.Options
    ) = plugin.transitionManager.migrate(
        player,
        road,
        source,
        options
    ).wasSuccessful()

    override fun tryTransition(
        player: Player,
        road: Road,
        prestigeRoad: Road,
        source: Transition.Source,
        options: Transition.Options
    ) = plugin.transitionManager.migrate(
        player,
        road,
        prestigeRoad,
        source,
        options
    ).wasSuccessful()
}