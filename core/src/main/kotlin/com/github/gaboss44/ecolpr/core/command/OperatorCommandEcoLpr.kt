package com.github.gaboss44.ecolpr.core.command

import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.command.CommandSender

class OperatorCommandEcoLpr(plugin: EcoLprPlugin) : PluginCommand(
    plugin,
    "ecolpr",
    "ecolpr.command.operator.ecolpr",
    false
) {
    init {
        this.addSubcommand(OperatorCommandReload(plugin))
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("invalid-command")
        )
    }
}