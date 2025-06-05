package com.github.gaboss44.ecolpr.wrapper

import com.github.gaboss44.ecolpr.EcoLprPlugin
import net.luckperms.api.context.ContextSet
import net.luckperms.api.model.group.Group
import net.luckperms.api.query.QueryOptions
import java.util.UUID

class GroupWrapper(
    plugin: EcoLprPlugin,
    group : Group
) : Wrapper<Group>(plugin, group) {
    val name = group.name
    val weight = group.weight

    val displayName = group.displayName

    fun hasMember(user : UserWrapper): Boolean =
        user.isInGroup(this)

    fun hasMember(user : UserWrapper, options: QueryOptions): Boolean =
        user.isInGroup(this, options)

    fun hasMember(user : UserWrapper, ctx : ContextSet): Boolean =
        user.isInGroup(this, ctx)

    fun hasMember(playerId: UUID): Boolean =
        plugin.repository.getUser(playerId)?.let { this.hasMember(it) } ?: false

    fun hasMember(playerId: UUID, options: QueryOptions): Boolean =
        plugin.repository.getUser(playerId)?.let { this.hasMember(it, options) } ?: false

    fun hasMember(playerId: UUID, ctx: ContextSet): Boolean =
        plugin.repository.getUser(playerId)?.let { this.hasMember(it, ctx) } ?: false
    
    fun isInTrack(track: String): Boolean =
        plugin.repository.getTrack(track)?.contains(this) ?: false

    fun isInTrack(track: TrackWrapper): Boolean =
        track.contains(this)
}