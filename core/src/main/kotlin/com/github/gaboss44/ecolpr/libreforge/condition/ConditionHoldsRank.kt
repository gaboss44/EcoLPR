package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.rank.Ranks
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.get
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player

object ConditionHoldsRank : QueryableCondition("holds_rank") {
    override val arguments = arguments {
        require("rank", "You must specify the rank!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: QueryOptions?
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val rank = Ranks.getByID(config.getString("rank").lowercase()) ?: return false
        return if (compileData == null) rank.isHeldBy(player) else rank.isHeldBy(player, compileData)
    }
}