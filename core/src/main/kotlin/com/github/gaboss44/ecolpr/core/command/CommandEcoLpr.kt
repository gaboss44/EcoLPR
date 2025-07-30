package com.github.gaboss44.ecolpr.core.command

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.asLpr
import com.github.gaboss44.ecolpr.core.model.road.Road
import com.github.gaboss44.ecolpr.core.model.road.Roads
import com.github.gaboss44.ecolpr.core.util.replacePlaceholders
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.toNumeral
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class CommandEcoLpr(plugin: EcoLprPlugin) : PluginCommand(
    plugin,
    "ecolpr",
    "ecolpr.command.ecolpr",
    false
) {
    init {
        this.addSubcommand(SubcommandReload(plugin))
            .addSubcommand(SubcommandSetPrestigeLevel(plugin))
            .addSubcommand(SubcommandGetPrestigeLevel(plugin))
            .addSubcommand(SubcommandRankup(plugin))
            .addSubcommand(SubcommandIngress(plugin))
            .addSubcommand(SubcommandAscend(plugin))
            .addSubcommand(SubcommandPrestige(plugin))
            .addSubcommand(SubcommandEgress(plugin))
            .addSubcommand(SubcommandRecurse(plugin))
            .addSubcommand(SubcommandMigrate(plugin))
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        plugin.asLpr().langYml.sendMessage(sender, "invalid-command")
    }

    class SubcommandReload(plugin: EcoLprPlugin) : Subcommand(
        plugin,
        "reload",
        "ecolpr.command.ecolpr.reload",
        false
    ) {
        override fun onExecute(sender: CommandSender, args: List<String>) {
            if (!Bukkit.isPrimaryThread()) return
            plugin.reload()
            plugin.asLpr().langYml.sendMessage(sender, "reloaded")
        }
    }

    abstract class PlayerRoadSelectSubcommand(
        plugin: EcoPlugin,
        name: String,
        permission: String,
        playersOnly: Boolean = false
    ) : Subcommand(
        plugin,
        name,
        permission,
        playersOnly
    ) {
        final override fun onExecute(sender: CommandSender, args: MutableList<String>) {
            if (!Bukkit.isPrimaryThread()) return

            val var0 = args.getOrNull(0) ?: run {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "required-player",
                    PlaceholderContext.EMPTY
                )
                return
            }

            val player = Bukkit.getPlayer(var0) ?: run {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "invalid-player",
                    PlaceholderContext.EMPTY
                ) { it.replace("%player%", var0) }
                return
            }

            val var1 = args.getOrNull(1) ?: run {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "required-road",
                    PlaceholderContext(player)
                )
                return
            }

            val road = Roads[var1] ?: run {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "invalid-road",
                    PlaceholderContext(player)
                ) { it.replace("%road%", var1) }
                return
            }

            onExecute(sender, player, road, args.drop(2).toMutableList())
        }

        protected abstract fun onExecute(
            sender: CommandSender,
            player: Player,
            road: Road,
            args: MutableList<String>
        )

        override fun tabComplete(sender: CommandSender, args: MutableList<String>): List<String> {
            if (args.size == 1) {
                return Bukkit.getOnlinePlayers().map { it.name }
            }

            if (args.size == 2) {
                return Roads.values().map { it.id }
            }

            return emptyList()
        }
    }

    class SubcommandSetPrestigeLevel(plugin: EcoLprPlugin) : PlayerRoadSelectSubcommand(
        plugin,
        "setprestigelevel",
        "ecolpr.command.ecolpr.setprestigelevel"
    ) {
        override fun onExecute(
            sender: CommandSender,
            player: Player,
            road: Road,
            args: MutableList<String>
        ) {
            if (args.isEmpty()) {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "required-prestige-level",
                    PlaceholderContext(player)
                )
                return
            }

            val levelStr = args[0]

            val level = levelStr.toIntOrNull()

            if (level == null || level < 0) {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "invalid-prestige-level",
                    PlaceholderContext(player)
                ) { it.replace("%prestige_level%", levelStr.toNiceString()) }
                return
            }

            road.setPrestigeLevel(player, level)

            plugin.asLpr().langYml.sendMessage(
                sender,
                "set-prestige-level-success.operator",
                PlaceholderContext(player)
            ) { it
                .replacePlaceholders(player, road)
                .replace("%prestige_level%", level.toNiceString())
                .replace("%prestige_level_numeral%", level.toNumeral())
            }
        }
    }

    class SubcommandGetPrestigeLevel(plugin: EcoLprPlugin) : PlayerRoadSelectSubcommand(
        plugin,
        "getprestigelevel",
        "ecolpr.command.ecolpr.getprestigelevel"
    ) {
        override fun onExecute(
            sender: CommandSender,
            player: Player,
            road: Road,
            args: MutableList<String>
        ) {
            val level = road.getPrestigeLevel(player)

            plugin.asLpr().langYml.sendMessage(
                sender,
                "get-prestige-level-success.operator",
                PlaceholderContext(player)
            ) { it
                .replacePlaceholders(player, road)
                .replace("%prestige_level%", level.toNiceString())
                .replace("%prestige_level_numeral%", level.toNumeral())
            }
        }
    }

    abstract class TransitionSubcommand(
        plugin: EcoPlugin,
        name: String,
        permission: String,
        playersOnly: Boolean = false,
    ) : PlayerRoadSelectSubcommand(
        plugin,
        name,
        permission,
        playersOnly
    ) {
        override fun onExecute(
            sender: CommandSender,
            player: Player,
            road: Road,
            args: MutableList<String>
        ) {
            if (plugin.asLpr().transitionManager.isLocked(player)) {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "transition-locked.operator",
                    PlaceholderContext(player)
                )
                return
            }

            val source = if (sender is ConsoleCommandSender) Transition.Source.CONSOLE else Transition.Source.ADMIN

            executeTransition(sender, player, road, source, args)
        }

        protected abstract fun executeTransition(
            sender: CommandSender,
            player: Player,
            road: Road,
            source: Transition.Source,
            args: MutableList<String>
        )
    }

    class SubcommandRankup(plugin: EcoLprPlugin) : TransitionSubcommand(
        plugin,
        "rankup",
        "ecolpr.command.ecolpr.rankup"
    ) {
        override fun executeTransition(
            sender: CommandSender,
            player: Player,
            road: Road,
            source: Transition.Source,
            args: MutableList<String>
        ) {
            val call = plugin.asLpr().transitionManager.rankup(
                player,
                road,
                source,
                Transition.Options.consumeArgs(args)
            )

            val result = call.result

            if (call.wasSuccessful() && result != null  && result.wasSuccessful()) {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "rankup-success.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, result) }
            } else {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "rankup-failure.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, road) }
            }
        }
    }

    class SubcommandIngress(plugin: EcoLprPlugin) : TransitionSubcommand(
        plugin,
        "ingress",
        "ecolpr.command.ecolpr.ingress"
    ) {
        override fun executeTransition(
            sender: CommandSender,
            player: Player,
            road: Road,
            source: Transition.Source,
            args: MutableList<String>
        ) {
            val call = plugin.asLpr().transitionManager.ingress(
                player,
                road,
                source,
                Transition.Options.consumeArgs(args)
            )

            val result = call.result

            if (call.wasSuccessful() && result != null  && result.wasSuccessful()) {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "ingress-success.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, result) }
            } else {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "ingress-failure.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, road) }
            }
        }
    }

    class SubcommandAscend(plugin: EcoLprPlugin) : TransitionSubcommand(
        plugin,
        "ascend",
        "ecolpr.command.ecolpr.ascend"
    ) {
        override fun executeTransition(
            sender: CommandSender,
            player: Player,
            road: Road,
            source: Transition.Source,
            args: MutableList<String>
        ) {
            val call = plugin.asLpr().transitionManager.ascend(
                player,
                road,
                source,
                Transition.Options.consumeArgs(args)
            )

            val result = call.result

            if (call.wasSuccessful() && result != null  && result.wasSuccessful()) {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "ascend-success.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, result) }
            } else {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "ascend-failure.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, road) }
            }
        }
    }

    class SubcommandPrestige(plugin: EcoLprPlugin) : TransitionSubcommand(
        plugin,
        "prestige",
        "ecolpr.command.ecolpr.prestige"
    ) {
        override fun executeTransition(
            sender: CommandSender,
            player: Player,
            road: Road,
            source: Transition.Source,
            args: MutableList<String>
        ) {
            val call = plugin.asLpr().transitionManager.prestige(
                player,
                road,
                source,
                Transition.Options.consumeArgs(args)
            )

            val result = call.result

            if (call.wasSuccessful() && result != null  && result.wasSuccessful()) {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "prestige-success.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, result) }
            } else {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "prestige-failure.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, road) }
            }
        }
    }

    class SubcommandEgress(plugin: EcoLprPlugin) : TransitionSubcommand(
        plugin,
        "egress",
        "ecolpr.command.ecolpr.egress"
    ) {
        override fun executeTransition(
            sender: CommandSender,
            player: Player,
            road: Road,
            source: Transition.Source,
            args: MutableList<String>
        ) {
            val call = plugin.asLpr().transitionManager.egress(
                player,
                road,
                source,
                Transition.Options.consumeArgs(args)
            )

            val result = call.result

            if (call.wasSuccessful() && result != null  && result.wasSuccessful()) {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "egress-success.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, result) }
            } else {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "egress-failure.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, road) }
            }
        }
    }

    class SubcommandRecurse(plugin: EcoLprPlugin) : TransitionSubcommand(
        plugin,
        "recurse",
        "ecolpr.command.ecolpr.recurse"
    ) {
        override fun executeTransition(
            sender: CommandSender,
            player: Player,
            road: Road,
            source: Transition.Source,
            args: MutableList<String>
        ) {
            val call = plugin.asLpr().transitionManager.recurse(
                player,
                road,
                source,
                Transition.Options.consumeArgs(args)
            )

            val result = call.result

            if (call.wasSuccessful() && result != null  && result.wasSuccessful()) {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "recurse-success.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, result) }
            } else {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "recurse-failure.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, road) }
            }
        }
    }

    class SubcommandMigrate(plugin: EcoLprPlugin) : TransitionSubcommand(
        plugin,
        "migrate",
        "ecolpr.command.ecolpr.migrate"
    ) {
        override fun executeTransition(
            sender: CommandSender,
            player: Player,
            road: Road,
            source: Transition.Source,
            args: MutableList<String>
        ) {
            val call = plugin.asLpr().transitionManager.migrate(
                player,
                road,
                source,
                Transition.Options.consumeArgs(args)
            )

            val result = call.result

            if (call.wasSuccessful() && result != null  && result.wasSuccessful()) {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "migrate-success.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, result) }
            } else {
                plugin.asLpr().langYml.sendMessage(
                    sender,
                    "migrate-failure.operator",
                    PlaceholderContext(player)
                ) { it.replacePlaceholders(player, road) }
            }
        }
    }
}
