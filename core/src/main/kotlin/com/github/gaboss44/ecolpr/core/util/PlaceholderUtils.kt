package com.github.gaboss44.ecolpr.core.util

import com.github.gaboss44.ecolpr.api.transition.Transition
import com.github.gaboss44.ecolpr.api.transition.generic.Prestige
import com.github.gaboss44.ecolpr.api.model.rank.Rank
import com.github.gaboss44.ecolpr.api.model.road.Road
import com.willfp.libreforge.NamedValue
import org.bukkit.entity.Player

fun Rank.namedValues(player: Player? = null) = listOf(
    NamedValue(
        "rank",
        name
    ),
    NamedValue(
        "rank_description",
        player?.let {
            getFormattedDescription(it)
        } ?: formattedDescription
    ),
    NamedValue(
        "rank_display_name",
        player?.let {
            getFormattedDisplayName(it)
        } ?: formattedDisplayName
    )
)

fun Road.namedValues(player: Player? = null) = listOf(
    NamedValue(
        "road",
        name
    ),
    NamedValue(
        "road_description",
        player?.let {
            getFormattedDescription(it)
        } ?: formattedDescription
    ),
    NamedValue(
        "road_display_name",
        player?.let {
            getFormattedDisplayName(it)
        } ?: formattedDisplayName
    )
)

fun Transition.namedValues(player: Player? = null) : List<NamedValue> {
    val list = mutableListOf(
        NamedValue(
            "road",
            road.name
        ),
        NamedValue(
            "road_description",
            player?.let {
                road.getFormattedDescription(it)
            } ?: road.formattedDescription
        ),
        NamedValue(
            "road_display_name",
            player?.let {
                road.getFormattedDisplayName(it)
            } ?: road.formattedDisplayName
        )
    )
    if (this is Transition.ToRank) list.apply {
        add(
            NamedValue(
                "to_rank",
                toRank.name
            )
        )
        add(
            NamedValue(
                "to_rank_description",
                player?.let {
                    toRank.getFormattedDescription(it)
                } ?: toRank.formattedDescription
            )
        )
        add(
            NamedValue(
                "to_rank_display_name",
                player?.let {
                    toRank.getFormattedDisplayName(it)
                } ?: toRank.formattedDisplayName
            )
        )
    }
    if (this is Transition.FromRank) list.apply {
        add(
            NamedValue(
                "from_rank",
                fromRank.name
            )
        )
        add(
            NamedValue(
                "from_rank_description",
                player?.let {
                    fromRank.getFormattedDescription(it)
                } ?: fromRank.formattedDescription
            )
        )
        add(
            NamedValue(
                "from_rank_display_name",
                player?.let {
                    fromRank.getFormattedDisplayName(it)
                } ?: fromRank.formattedDisplayName
            )
        )
    }
    if (this is Prestige) list.apply {
        add(
            NamedValue(
                "prestige_level",
                prestigeLevel
            )
        )
    }
    if (this is Prestige.WithTarget) list.apply {
        add(
            NamedValue(
                "prestige_road",
                prestigeRoad.name
            )
        )
        add(
            NamedValue(
                "prestige_road_description",
                player?.let {
                    prestigeRoad.getFormattedDescription(it)
                } ?: prestigeRoad.formattedDescription
            )
        )
        add(
            NamedValue(
                "prestige_road_display_name",
                player?.let {
                    prestigeRoad.getFormattedDisplayName(it)
                } ?: prestigeRoad.formattedDisplayName
            )
        )
    }

    return list.toList()
}

fun Rank.replacePlaceholders(
    player: Player? = null,
    string: String
): String {
    return string
        .replace("%rank%", name)
        .replace("%rank_description%", player?.let { getFormattedDescription(it) } ?: formattedDescription)
        .replace("%rank_display_name%", player?.let { getFormattedDisplayName(it) } ?: formattedDisplayName)
}

fun Road.replacePlaceholders(
    player: Player? = null,
    string: String
): String {
    return string
        .replace("%road%", name)
        .replace("%road_description%", player?.let { getFormattedDescription(it) } ?: formattedDescription)
        .replace("%road_display_name%", player?.let { getFormattedDisplayName(it) } ?: formattedDisplayName)
}

fun Transition.replacePlaceholders(
    player: Player? = null,
    string: String
): String {
    var replaced = string
        .replace("%road%", road.name)
        .replace("%road_description%", player?.let { road.getFormattedDescription(it) } ?: road.formattedDescription)
        .replace("%road_display_name%", player?.let { road.getFormattedDisplayName(it) } ?: road.formattedDisplayName)

    if (this is Transition.ToRank) {
        replaced = replaced
            .replace("%to_rank%", toRank.name)
            .replace("%to_rank_description%", player?.let { toRank.getFormattedDescription(it) } ?: toRank.formattedDescription)
            .replace("%to_rank_display_name%", player?.let { toRank.getFormattedDisplayName(it) } ?: toRank.formattedDisplayName)
    }

    if (this is Transition.FromRank) {
        replaced = replaced
            .replace("%from_rank%", fromRank.name)
            .replace("%from_rank_description%", player?.let { fromRank.getFormattedDescription(it) } ?: fromRank.formattedDescription)
            .replace("%from_rank_display_name%", player?.let { fromRank.getFormattedDisplayName(it) } ?: fromRank.formattedDisplayName)
    }

    if (this is Prestige) {
        replaced = replaced
            .replace("%prestige_level%", prestigeLevel.toString())
    }

    if (this is Prestige.WithTarget) {
        replaced = replaced
            .replace("%prestige_road%", prestigeRoad.name)
            .replace("%prestige_road_description%", player?.let { prestigeRoad.getFormattedDescription(it) } ?: prestigeRoad.formattedDescription)
            .replace("%prestige_road_display_name%", player?.let { prestigeRoad.getFormattedDisplayName(it) } ?: prestigeRoad.formattedDisplayName)
    }

    return replaced
}