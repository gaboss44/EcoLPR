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

object ConditionPlayerOnRoad : Condition<NoCompileData>("ecolpr_player_on_road") {
    override val arguments = arguments {
        require("road", "You must specify a road")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val road = Roads[config.getString("road")] ?: return false
        val value = config.getBoolOrNull("value") ?: true
        return road.getRanks(player).isNotEmpty() == value
    }
}