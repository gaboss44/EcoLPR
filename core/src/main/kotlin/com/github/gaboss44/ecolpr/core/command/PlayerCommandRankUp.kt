package com.github.gaboss44.ecolpr.core.command

import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.exception.transition.TransitionException
import com.github.gaboss44.ecolpr.libreforge.config.RoadCategory
import com.github.gaboss44.ecolpr.core.util.replaceWith
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.entity.Player

class PlayerCommandRankUp(private val plugin: EcoLprPlugin) : PluginCommand(
    plugin,
    "up",
    "ecolpr.command.player.rankup",
    true
) {
    override fun onExecute(player: Player, args: List<String>) {
        val var0 = args.getOrNull(0)
        if (var0 == null || var0.isEmpty()) {
            plugin.langYml.sendMessage(
                player,
                "required-road"
            )
            return
        }

        val road = RoadCategory.getByIdOverride(var0) ?: run {
            plugin.langYml.sendMessage(
                player,
                "invalid-road"
            ) { it.replace("%road%", var0) }
            return
        }

        val apply: (String) -> String = { str: String ->
            str.replace("%road%", road.id)
                .replace("%road_id_override%", road.luckpermsName)
                .replace("%road_description%", road.description)
                .replace("%road_display_name%", road.displayName)
        }

        if (!road.isVisibleFor(player)) {
            plugin.langYml.sendMessage(
                player,
                "unsatisfied-road"
            ) { apply(it) }
            return
        }
        else if (!road.isSatisfiedBy(player)) {
            plugin.langYml.sendMessage(
                player,
                "unsatisfied-road"
            ) { apply(it) }
            return
        }
        try {
            val result = plugin.rankTransitioner.promote(player, road)
            if (result.wasSuccessful) {
                plugin.langYml.sendMessage(
                    player,
                    "rank-ascension-successful.player"
                ) { it.replaceWith(result.transition) }
            }
            else {
                plugin.langYml.sendMessage(
                    player,
                    "rank-ascension-cancelled.player"
                ) { it.replaceWith(result.transition) }
            }
        } catch (_: TransitionException) {
                plugin.langYml.sendMessage(
                    player,
                    "rank-ascension-failed.player"
                ) { apply(it) }
        }
    }
}
