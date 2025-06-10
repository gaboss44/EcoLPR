package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.rank.RankCategory
import com.github.gaboss44.ecolpr.util.queryOptions
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionIsHoldingSomeRank : Condition<NoCompileData>("is_holding_some_rank") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val queryOptions = config.getSubsectionOrNull("query-options")?.queryOptions

        return RankCategory.values().any { rank ->
            if (queryOptions == null || queryOptions.context().isEmpty) {
                rank.isHeldBy(player)
            } else {
                rank.isHeldBy(player, queryOptions)
            }
        }
    }
}