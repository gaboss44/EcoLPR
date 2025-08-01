package com.github.gaboss44.ecolpr.core.libreforge.condition

import com.github.gaboss44.ecolpr.core.model.rank.Ranks
import com.github.gaboss44.ecolpr.core.model.road.Roads
import com.github.gaboss44.ecolpr.core.util.MatchMode
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getStrings
import org.bukkit.entity.Player

object ConditionHasRank : Condition<NoCompileData>("ecolpr_has_rank") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val ranks = config.getStrings("ranks", "rank").mapNotNull { Ranks[it] }
        val road = config.getStringOrNull("road")?.let { Roads[it] ?: return false }
        val mode = MatchMode[config.getStringOrNull("match-mode")] ?: MatchMode.ANY
        return mode.evaluate(ranks) { it.isHeldBy(player, road?.contextSet) }
    }
}