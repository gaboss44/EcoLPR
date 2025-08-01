package com.github.gaboss44.ecolpr.core.command

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.asLpr
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.util.replacePlaceholders
import org.bukkit.entity.Player

class CommandPrestige(plugin: EcoLprPlugin) : PlayerTransitionCommand(
    plugin,
    "prestige",
    "ecolpr.command.prestige"
) {
    override fun executeTransition(
        player: Player,
        road: Road,
        source: Transition.Source,
        args: List<String>
    ) {
        val call = plugin.asLpr().transitionManager.prestige(player, road, source)

        val result = call.result

        if (call.wasSuccessful() && result != null && result.wasSuccessful()) {
            plugin.asLpr().langYml.sendMessage(
                player,
                "prestige-success.player"
            ) { replacePlaceholders(player, result) }
        } else {
            plugin.asLpr().langYml.sendMessage(
                player,
                "prestige-failure.player"
            ) { replacePlaceholders(player, road) }
        }
    }
}
