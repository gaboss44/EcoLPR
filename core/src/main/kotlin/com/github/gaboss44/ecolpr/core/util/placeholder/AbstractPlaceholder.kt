package com.github.gaboss44.ecolpr.core.util.placeholder

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.Placeholder
import java.util.regex.Pattern

abstract class AbstractPlaceholder(
    var plugin: EcoPlugin,
    var pattern: Pattern
) : Placeholder {

    override fun getPlugin() = plugin

    override fun getPattern() = pattern

}