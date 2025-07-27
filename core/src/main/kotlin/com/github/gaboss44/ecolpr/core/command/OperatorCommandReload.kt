package com.github.gaboss44.ecolpr.core.command

import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.willfp.eco.core.command.impl.Subcommand
import org.bukkit.command.CommandSender

class OperatorCommandReload(plugin: EcoLprPlugin) : Subcommand(
    plugin,
    "reload",
    "ecolpr.command.operator.reload",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        plugin.reload()
        sender.sendMessage(plugin.langYml.getMessage("reloaded"))
    }
}