package com.github.gaboss44.ecolpr.core.util

import com.github.gaboss44.ecolpr.core.plugin
import com.willfp.eco.core.config.interfaces.Config
import net.luckperms.api.cacheddata.CachedPermissionData
import net.luckperms.api.context.ContextSet
import net.luckperms.api.context.MutableContextSet
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.user.User
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player

fun QueryOptions.context(
    contextSet: ContextSet
) = this.toBuilder()
    .context(contextSet)
    .build()

fun CachedPermissionData.hasPermission(permission: String) = this.checkPermission(permission).asBoolean()

fun User.getQueryOptions(
    contextSet: ContextSet? = null
) = contextSet
    ?.let { this.queryOptions.context(it) }
    ?: this.queryOptions

fun User.getPermissionData(
    contextSet: ContextSet? = null
) = this.cachedData.getPermissionData(this.getQueryOptions(contextSet))

fun User.getInheritedGroups(
    contextSet: ContextSet? = null
) : Collection<Group> = this.getInheritedGroups(getQueryOptions(contextSet))

fun User.getInheritedGroups(
    contextSet: ContextSet? = null,
    groupsToMatch: List<String>
) = this.getInheritedGroups(contextSet).filter { groupsToMatch.contains(it.name) }

val User.inheritedGroups get() = this.getInheritedGroups()

fun User.checkGroupNode(
    groupName: String,
    contextSet: ContextSet? = null
) = this.getPermissionData(contextSet).checkPermission("group.$groupName")

fun User.checkGroupNode(
    group: Group,
    contextSet: ContextSet? = null
) = this.checkGroupNode(group.name, contextSet)

fun User.hasGroupNode(
    groupName: String,
    contextSet: ContextSet? = null
) = this.checkGroupNode(groupName, contextSet).asBoolean()

fun User.hasGroupNode(
    group: Group,
    contextSet: ContextSet? = null
) = this.checkGroupNode(group, contextSet).asBoolean()

fun Player.asUser() = plugin.luckperms.getUser(this)

fun Player.getPermissionData(
    contextSet: ContextSet? = null
) = this.asUser().getPermissionData(contextSet)

fun Player.checkGroupNode(
    groupName: String,
    contextSet: ContextSet? = null
) = this.asUser().checkGroupNode(groupName, contextSet)

fun Player.checkGroupNode(
    group: Group,
    contextSet: ContextSet? = null
) = this.checkGroupNode(group.name, contextSet)

fun Player.hasGroupNode(
    groupName: String,
    contextSet: ContextSet? = null
) = this.checkGroupNode(groupName, contextSet).asBoolean()

fun Player.hasGroupNode(
    group: Group,
    contextSet: ContextSet? = null
) = this.checkGroupNode(group, contextSet).asBoolean()