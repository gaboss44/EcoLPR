package com.github.gaboss44.ecolpr.core.util.placeholder

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.RegistrablePlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.PatternUtils
import java.util.regex.Pattern

class ContextualPlaceholder (
    plugin: EcoPlugin,
    pattern: Pattern,
    private val provider: (String, PlaceholderContext) -> String?
) : AbstractPlaceholder(
    plugin,
    pattern
), RegistrablePlaceholder, RegexTranslatablePlaceholder {

    constructor(
        plugin: EcoPlugin,
        identifier: String,
        provider: (String, PlaceholderContext) -> String?
    ) : this(
        plugin,
        PatternUtils.compileLiteral(identifier),
        provider
    )

    override fun getValue(
        args: String,
        context: PlaceholderContext
    ) = provider.invoke(
        args,
        context
    )

}
