package com.github.gaboss44.ecolpr.rank

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.wrapper.GroupWrapper
import com.willfp.eco.core.config.interfaces.Config
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player
import java.util.UUID

class RankImpl(
    override val id: String,
    config : Config,
    private val plugin: EcoLprPlugin
) : Rank {
    override val idOverride: String = config.getString("id-override").takeUnless { it.isEmpty() } ?: id

    override val displayName: String? = config.getString("display-name")

    val underlyingGroup: GroupWrapper? get() = plugin.repository.getGroup(idOverride)

    override val inheritedDisplayName: String? get() = underlyingGroup?.displayName

    override fun isHeldBy(player: Player): Boolean =
        plugin.repository.getUser(player).permissionData.checkPermission("group.$idOverride").asBoolean()

    override fun isHeldBy(player: Player, road: Road): Boolean =
        plugin.repository.getUser(player).getPermissionData(road.queryOptions).checkPermission("group.$idOverride").asBoolean()

    override fun isHeldBy(player: Player, queryOptions: QueryOptions): Boolean =
        plugin.repository.getUser(player).getPermissionData(queryOptions).checkPermission("group.$idOverride").asBoolean()

    override fun isHeldBy(playerId: UUID): Boolean =
        plugin.repository.getUser(playerId)?.permissionData?.checkPermission("group.$idOverride")?.asBoolean()
            ?: false

    override fun isHeldBy(playerId: UUID, road: Road): Boolean =
        plugin.repository.getUser(playerId)?.getPermissionData(road.queryOptions)?.checkPermission("group.$idOverride")?.asBoolean()
            ?: false

    override fun isHeldBy(playerId: UUID, queryOptions: QueryOptions): Boolean =
        plugin.repository.getUser(playerId)?.getPermissionData(queryOptions)?.checkPermission("group.$idOverride")?.asBoolean()
            ?: false
}