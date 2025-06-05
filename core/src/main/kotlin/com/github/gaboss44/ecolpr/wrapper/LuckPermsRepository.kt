package com.github.gaboss44.ecolpr.wrapper

import com.github.gaboss44.ecolpr.EcoLprPlugin
import net.luckperms.api.LuckPerms
import net.luckperms.api.platform.PlayerAdapter
import net.luckperms.api.query.QueryMode
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player
import java.util.UUID

class LuckPermsRepository(
    plugin: EcoLprPlugin,
    luckperms: LuckPerms
) : Wrapper<LuckPerms>(plugin, luckperms) {
    private val adapter: PlayerAdapter<Player> = luckperms.getPlayerAdapter(Player::class.java)

    val staticQueryOptions: QueryOptions
        get() = obj.contextManager.staticQueryOptions

    fun getTrack(name: String): TrackWrapper? =
        obj.trackManager.getTrack(name) ?.let { TrackWrapper(plugin, it) }

    fun getGroup(name: String): GroupWrapper? =
        obj.groupManager.getGroup(name) ?.let { GroupWrapper(plugin, it) }

    fun getUser(player: Player): UserWrapper =
        UserWrapper(plugin, adapter.getUser(player))

    fun getUser(playerId: UUID): UserWrapper? =
        obj.userManager.getUser(playerId) ?.let { UserWrapper(plugin, it) }

    fun getUser(name: String): UserWrapper? =
        obj.userManager.getUser(name) ?.let { UserWrapper(plugin, it) }

    fun queryOptionsBuilder(mode: QueryMode) : QueryOptions.Builder =
        obj.contextManager.queryOptionsBuilder(mode)
}