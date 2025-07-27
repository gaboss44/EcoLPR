package com.github.gaboss44.ecolpr.core.command

import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.willfp.eco.core.command.impl.PluginCommand

class PlayerCommandRank(private val plugin: EcoLprPlugin) : PluginCommand(
    plugin,
    "recurse",
    "ecolpr.command.player.rank",
    true
)