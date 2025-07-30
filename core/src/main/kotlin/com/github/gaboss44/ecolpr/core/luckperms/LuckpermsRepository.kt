package com.github.gaboss44.ecolpr.core.luckperms

import com.github.gaboss44.ecolpr.core.util.GroupContext
import net.luckperms.api.LuckPerms
import net.luckperms.api.model.user.User
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class LuckpermsRepository(
    private val luckperms: LuckPerms
) {

    private val adapter = luckperms
        .getPlayerAdapter(Player::class.java)

    val staticQueryOptions get() = luckperms
        .contextManager
        .staticQueryOptions

    fun getGroup(name: String) = luckperms
        .groupManager
        .getGroup(name)

    fun isGroupLoaded(group: String) = luckperms.groupManager.isLoaded(group)

    fun transferGroup(
        player: Player,
        remove: GroupContext? = null,
        add: GroupContext? = null,
    ) : CompletableFuture<Void> = this.getUser(player).let { user ->
        if (remove != null) user.data().remove(
            InheritanceNode
                .builder(remove.group)
                .context(remove.context)
                .build()
        )
        if (add != null) user.data().add(
            InheritanceNode
                .builder(add.group)
                .context(add.context)
                .build()
        )
        saveUser(user)
    }

    fun getUser(player: Player) = adapter.getUser(player)

    fun saveUser(user: User) = luckperms.userManager.saveUser(user)

}