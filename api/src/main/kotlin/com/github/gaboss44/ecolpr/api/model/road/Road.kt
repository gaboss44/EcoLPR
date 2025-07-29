package com.github.gaboss44.ecolpr.api.model.road

import com.github.gaboss44.ecolpr.api.model.rank.Rank
import com.github.gaboss44.ecolpr.api.util.traits.Describable
import com.github.gaboss44.ecolpr.api.util.traits.Displayable
import net.luckperms.api.context.ContextSet
import org.bukkit.entity.Player
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
interface Road : Displayable, Describable {

    val name: String

    val contextSet: ContextSet

    val ranks: List<Rank>

    fun getRanks(player: Player) : List<Rank>

    fun getPrestigeLevel(player: Player) : Int

    val prestigeTarget: String?

    val prestigeType: PrestigeType?

    val isHidden: Boolean

    val hideBypassPermission: String?

    fun isVisibleFor(player: Player) = !isHidden || hideBypassPermission
        ?.let { player.hasPermission(it) } ?: false

}
