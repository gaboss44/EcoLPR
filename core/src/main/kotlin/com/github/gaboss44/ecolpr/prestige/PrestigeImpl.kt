package com.github.gaboss44.ecolpr.prestige

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.wrapper.TrackWrapper
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.toNiceString
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player

class PrestigeImpl(
    override val id: String,
    config: Config,
    private val plugin: EcoLprPlugin
): Prestige {
    init {
        PlayerPlaceholder(plugin, "${id}_id_override") { idOverride }.register()
        PlayerPlaceholder(plugin, "${id}_display_name") { displayName?.toNiceString() ?: "" }.register()
    }

    override val idOverride: String = config.getString("id-override").takeUnless { it.isEmpty() } ?: id

    override val displayName: String? = config.getString("display-name")

    val underlyingTrack: TrackWrapper? get() = plugin.repository.getTrack(idOverride)

    override val groups: List<String>? get() = underlyingTrack?.groups

    override fun getLevel(player: Player, queryOptions: QueryOptions): Int =
        groups?.let {
            val data = plugin.repository.getUser(player).getPermissionData(queryOptions)
            for (string in it) {
                if (data.checkPermission("group.$string").asBoolean()) {
                    return it.indexOf(string)
                }
            }
            0
        } ?: -1
}