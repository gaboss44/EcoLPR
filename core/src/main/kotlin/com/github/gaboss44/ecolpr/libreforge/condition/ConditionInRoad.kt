package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.rank.Roads
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import net.luckperms.api.query.QueryOptions

object ConditionInRoad : QueryableCondition("in_road") {
    override val arguments = arguments {
        require("rank", "You must specify the rank!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: QueryOptions
    ): Boolean {

        val road = Roads.getByID(config.getString("road").lowercase()) ?: return false

        return road.isInRoad(dispatcher, compileData)
    }
}