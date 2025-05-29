package com.github.gaboss44.ecolpr.wrapper

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.util.QueryUtils
import net.luckperms.api.context.ContextSet
import net.luckperms.api.model.user.User

class UserWrapper(
    plugin : EcoLprPlugin,
    user : User
) : Wrapper<User>(plugin, user) {
    val uniqueId = user.uniqueId
    val username = user.username
    val primaryGroup = user.primaryGroup

    fun isInTrack(track : String) : Boolean =
        plugin.repo.getTrack(track)?.let { this.isInTrack(it) } ?: false

    fun isInTrack(track : TrackWrapper) : Boolean =
        track.groups.any { this.isInGroup(it) }

    fun isInGroup(group : String) : Boolean =
        obj.cachedData.permissionData.checkPermission("group.$group").asBoolean()

    fun isInGroup(group: String, vararg ctx: String): Boolean =
        obj.cachedData.getPermissionData(QueryUtils.options(*ctx)).checkPermission("group.$group").asBoolean()

    fun isInGroup(group: String, ctx: List<String>): Boolean =
        obj.cachedData.getPermissionData(QueryUtils.options(ctx)).checkPermission("group.$group").asBoolean()

    fun isInGroup(group: String, ctx: ContextSet): Boolean =
        obj.cachedData.getPermissionData(QueryUtils.options(ctx)).checkPermission("group.$group").asBoolean()
}

