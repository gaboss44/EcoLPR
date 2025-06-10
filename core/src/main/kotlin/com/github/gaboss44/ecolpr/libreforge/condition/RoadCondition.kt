package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.road.Road
import com.github.gaboss44.ecolpr.road.RoadCategory
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.getStrings

abstract class RoadCondition(id: String) : Condition<List<Road>>(id) {
    override val arguments = arguments {
        require(listOf("roads", "road"), "You must specify the road(s)!")
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<Road> =
        config.getStrings("roads", "road").let {
            RoadCategory.values().filter { road -> it.containsIgnoreCase(road.id) }
        }
}