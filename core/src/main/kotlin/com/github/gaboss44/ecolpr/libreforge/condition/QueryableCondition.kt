package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.util.queryOptions
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Condition
import net.luckperms.api.query.QueryOptions

abstract class QueryableCondition(id: String) : Condition<QueryOptions?>(id) {
    override fun makeCompileData(config: Config, context: ViolationContext): QueryOptions? =
        config.getSubsectionOrNull("query-options")?.queryOptions
}