package com.github.gaboss44.ecolpr.core.libreforge.condition

import com.github.gaboss44.ecolpr.core.model.road.Roads
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

abstract class PrestigeLevelCondition(id: String) : Condition<NoCompileData>(id) {
    final override val arguments = arguments {
        require("road", "You must specify a road")
        require("value", "You must specify the prestige level")
    }

    final override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val road = Roads[config.getStringOrNull("road")] ?: return false
        val level = road.getPrestigeLevel(player)
        val value = config.getIntFromExpression("value", player)
        return compare(level, value)
    }

    abstract fun compare(level: Int, value: Int): Boolean
}