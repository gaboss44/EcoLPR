package com.github.gaboss44.ecolpr.rank

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.road.Road
import com.github.gaboss44.ecolpr.wrapper.GroupWrapper
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.Placeholder
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.toNiceString
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player

class RankImpl(
    override val id: String,
    config : Config,
    private val plugin: EcoLprPlugin
) : Rank {
    init {
        PlayerPlaceholder(plugin, "${id}_id_override") { idOverride }.register()
        PlayerPlaceholder(plugin, "${id}_display_name") { displayName?.toNiceString() ?: "" }.register()
        PlayerPlaceholder(plugin, "${id}_inherited_display_name") { inheritedDisplayName?.toNiceString() ?: "" }.register()
    }

    override val idOverride: String = config.getString("id-override").takeUnless { it.isEmpty() } ?: id

    override val displayName: String? = config.getString("display-name")

    val underlyingGroup: GroupWrapper? get() = plugin.repository.getGroup(idOverride)

    override val inheritedDisplayName: String? get() = underlyingGroup?.displayName

    override fun isHeldBy(player: Player): Boolean =
        plugin.repository.getUser(player).permissionData.checkPermission(groupNode).asBoolean()

    override fun isHeldBy(player: Player, road: Road): Boolean =
        plugin.repository.getUser(player).getPermissionData(road.queryOptions).checkPermission(groupNode).asBoolean()

    override fun isHeldBy(player: Player, queryOptions: QueryOptions): Boolean =
        plugin.repository.getUser(player).getPermissionData(queryOptions).checkPermission(groupNode).asBoolean()
}