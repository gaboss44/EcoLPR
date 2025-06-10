package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.RankCategory
import com.github.gaboss44.ecolpr.util.queryOptions
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionHeldRanks : RankCondition("held_ranks") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: List<Rank>
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val mode = EvaluationMode.of(config.getString("mode")) ?: EvaluationMode.ANY
        val queryOptions = config.getSubsectionOrNull("query-options")?.queryOptions

        return when(mode) {
            EvaluationMode.ANY -> RankCategory.values().any { rank ->
                if (queryOptions == null || queryOptions.context().isEmpty) {
                    rank.isHeldBy(player)
                } else {
                    rank.isHeldBy(player, queryOptions)
                }
            }
            EvaluationMode.ALL -> RankCategory.values().all { rank ->
                if (queryOptions == null || queryOptions.context().isEmpty) {
                    rank.isHeldBy(player)
                } else {
                    rank.isHeldBy(player, queryOptions)
                }
            }
        }
    }
}