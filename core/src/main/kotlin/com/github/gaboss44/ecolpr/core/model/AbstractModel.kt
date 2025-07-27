package com.github.gaboss44.ecolpr.core.model

import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.api.util.traits.Describable
import com.github.gaboss44.ecolpr.api.util.traits.Displayable
import com.github.gaboss44.ecolpr.core.util.placeholder.ContextualPlaceholder
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.Placeholder
import com.willfp.eco.core.registry.KRegistrable

abstract class AbstractModel(
    protected val plugin: EcoLprPlugin,
    override val id: String,
    protected val config: Config,
    val category: String
) : KRegistrable {

    val prefix = category + "_" + id

    protected val placeholders = mutableListOf<Placeholder>().apply {
        if (this@AbstractModel is Displayable) add(
            ContextualPlaceholder(
                plugin,
                prefix + "_display_name"
            ) { _, context -> getFormattedDisplayName(context) } .register()
        )
        if (this@AbstractModel is Describable) add(
            ContextualPlaceholder(
                plugin,
                prefix + "_description"
            ) { _, context -> getFormattedDescription(context) } .register()
        )
    }
}