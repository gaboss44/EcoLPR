package com.github.gaboss44.ecolpr.core.command

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.asLpr
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.model.road.Roads
import com.github.gaboss44.ecolpr.core.util.replacePlaceholders
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player

abstract class PlayerTransitionCommand(
    plugin: EcoLprPlugin,
    name: String,
    permission: String,
) : PluginCommand(plugin, name, permission, true) {

    override fun onExecute(player: Player, args: List<String>) {

        if (!Bukkit.isPrimaryThread()) return

        if (plugin.asLpr().transitionManager.isLocked(player)) {
            plugin.asLpr().langYml.sendMessage(player, "transition-locked.player")
            return
        }

        val arg0 = args.getOrNull(0) ?: run {
            plugin.asLpr().langYml.sendMessage(player, "required-road")
            return
        }

        val road = Roads[arg0] ?: run {
            plugin.asLpr().langYml.sendMessage(player, "invalid-road") {
                it.replace("%road%", arg0)
            }
            return
        }

        if (!road.isVisibleFor(player)) {
            plugin.asLpr().langYml.sendMessage(player, "hidden-road") {
                it.replacePlaceholders(player, road)
            }
            return
        }

        val source = Transition.Source.PLAYER

        executeTransition(player, road, source, args.drop(1))
    }

    protected abstract fun executeTransition(
        player: Player,
        road: Road,
        source: Transition.Source,
        args: List<String>
    )

    override fun tabComplete(sender: Player, args: List<String>): List<String> {
        if (args.size == 1) {
            return Roads.values()
                .filter { it.isVisibleFor(sender) }
                .map { it.id }
                .filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}