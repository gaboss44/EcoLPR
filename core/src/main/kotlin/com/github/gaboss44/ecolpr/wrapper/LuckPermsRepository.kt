package com.github.gaboss44.ecolpr.wrapper

import com.github.gaboss44.ecolpr.EcoLprPlugin
import net.luckperms.api.LuckPerms
import net.luckperms.api.platform.PlayerAdapter
import net.luckperms.api.query.QueryMode
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.CompletableFuture

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

    fun loadUser(uuid: UUID): CompletableFuture<UserWrapper?> =
        obj.userManager.loadUser(uuid).thenApply { user -> user?.let { UserWrapper(plugin, user) } }

    fun saveUser(user: UserWrapper): CompletableFuture<Void> =
        obj.userManager.saveUser(user.obj)

    fun queryOptionsBuilder(mode: QueryMode) : QueryOptions.Builder =
        obj.contextManager.queryOptionsBuilder(mode)
}