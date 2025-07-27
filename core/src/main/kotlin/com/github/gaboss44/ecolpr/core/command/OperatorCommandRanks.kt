package com.github.gaboss44.ecolpr.core.command

import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.willfp.eco.core.command.impl.Subcommand

class OperatorCommandRanks(plugin: EcoLprPlugin):
    Subcommand(
        plugin,
        "ranks",
        "ecolpr.command.operator.ranks",
        false
    ) {
}