package com.github.gaboss44.ecolpr.api.util.traits

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.util.StringUtils
import org.bukkit.entity.Player

interface Displayable {

    val displayName: String

    val formattedDisplayName
        get() = StringUtils.format(displayName)

    fun getFormattedDisplayName(
        player: Player
    ) = StringUtils.format(
        displayName,
        player
    )

    fun getFormattedDisplayName(
        context: PlaceholderContext
    ) = StringUtils.format(
        displayName,
        context
    )

}