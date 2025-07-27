package com.github.gaboss44.ecolpr.api.util.traits

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.StringUtils
import org.bukkit.entity.Player

interface Describable {

    val description: String

    val formattedDescription get() = StringUtils.format(description)

    fun getFormattedDescription(
        player: Player
    ) = StringUtils.format(
        description,
        player
    )

    fun getFormattedDescription(
        context: PlaceholderContext
    ) = StringUtils.format(
        description,
        context
    )

}