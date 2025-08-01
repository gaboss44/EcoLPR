package com.github.gaboss44.ecolpr.core.command

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.asLpr
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.util.replacePlaceholders
import org.bukkit.entity.Player

class CommandRankup(plugin: EcoLprPlugin) : PlayerTransitionCommand(
    plugin,
    "rankup",
    "ecolpr.command.rankup"
) {
    override fun executeTransition(
        player: Player,
        road: Road,
        source: Transition.Source,
        args: List<String>
    ) {
        val call = plugin.asLpr().transitionManager.rankup(player, road, source)

        val result = call.result

        if (call.wasSuccessful() && result != null && result.wasSuccessful()) {
            plugin.asLpr().langYml.sendMessage(
                player,
                "rankup-success.player"
            ) { replacePlaceholders(player, result) }
        } else {
            plugin.asLpr().langYml.sendMessage(
                player,
                "rankup-failure.player"
            ) { replacePlaceholders(player, road) }
        }
    }
}
