package com.github.gaboss44.ecolpr.core.libreforge.condition

import com.github.gaboss44.ecolpr.core.util.parseContextSet
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.Condition
import net.luckperms.api.context.ContextSet

abstract class ContextualCondition(id: String) : Condition<ContextSet?>(id) {
    override fun makeCompileData(
        config: Config,
        context: ViolationContext
    ) = config.getSubsectionOrNull("context-set")
        ?.let{ parseContextSet(it) }
}