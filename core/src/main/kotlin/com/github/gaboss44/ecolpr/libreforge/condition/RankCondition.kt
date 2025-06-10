package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.rank.Rank
import com.github.gaboss44.ecolpr.rank.RankCategory
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.getStrings

abstract class RankCondition(id: String) : Condition<List<Rank>>(id) {
    override val arguments = arguments {
        require(listOf("ranks", "rank"), "You must specify the rank(s)!")
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<Rank> =
        config.getStrings("ranks", "rank").let {
            RankCategory.values().filter { rank -> it.containsIgnoreCase(rank.id) }
        }
}