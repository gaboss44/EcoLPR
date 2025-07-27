package com.github.gaboss44.ecolpr.api.model.rank

import com.github.gaboss44.ecolpr.api.util.traits.Describable
import com.github.gaboss44.ecolpr.api.util.traits.Displayable
import com.github.gaboss44.ecolpr.api.model.road.Road
import net.luckperms.api.context.ContextSet
import org.bukkit.entity.Player
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Rank : Displayable, Describable {

    val name: String

    val group: String

    /** Checks if this [Rank]'s related [net.luckperms.api.model.group.Group] is active (loaded) */
    fun isGroupActive(): Boolean

    fun isHeldBy(
        player: Player
    ) = this.isHeldBy(
        player,
        null
    )

    fun isHeldBy(
        player: Player,
        contextSet: ContextSet?
    ): Boolean

    fun isHeldBy(
        player: Player,
        road: Road
    ) = this.isHeldBy(
        player,
        road.contextSet
    )
}
