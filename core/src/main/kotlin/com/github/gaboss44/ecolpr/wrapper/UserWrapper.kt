package com.github.gaboss44.ecolpr.wrapper

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.util.options
import net.luckperms.api.cacheddata.CachedPermissionData
import net.luckperms.api.context.ContextSet
import net.luckperms.api.model.user.User
import net.luckperms.api.query.QueryOptions

class UserWrapper(
    plugin : EcoLprPlugin,
    user : User
) : Wrapper<User>(plugin, user) {
    val uniqueId = user.uniqueId
    val username = user.username
    val primaryGroup = user.primaryGroup
    val permissionData: CachedPermissionData get() = obj.cachedData.permissionData
    val queryOptions: QueryOptions get() = obj.queryOptions
    val context: ContextSet get() = queryOptions.context()

    fun getPermissionData(options: QueryOptions): CachedPermissionData =
        obj.cachedData.getPermissionData(options)

    fun isInTrack(track : String) : Boolean =
        plugin.repository.getTrack(track)?.let { this.isInTrack(it) } ?: false

    fun isInTrack(track : TrackWrapper) : Boolean =
        track.groups.any { this.isInGroup(it) }

    fun isInGroup(group : String) : Boolean =
        obj.cachedData.permissionData.checkPermission("group.$group").asBoolean()

    fun isInGroup(group: GroupWrapper): Boolean =
        this.isInGroup(group.name)

    fun isInGroup(group: String, options: QueryOptions): Boolean =
        obj.cachedData.getPermissionData(options).checkPermission("group.$group").asBoolean()

    fun isInGroup(group: GroupWrapper, options: QueryOptions): Boolean =
        this.isInGroup(group.name, options)

    fun isInGroup(group: String, ctx: ContextSet): Boolean =
        this.isInGroup(group, options(ctx))

    fun isInGroup(group: GroupWrapper, ctx: ContextSet): Boolean =
        this.isInGroup(group.name, options(ctx))
}

