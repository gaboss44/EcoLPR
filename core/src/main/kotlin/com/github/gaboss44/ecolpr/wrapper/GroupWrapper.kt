package com.github.gaboss44.ecolpr.wrapper

import com.github.gaboss44.ecolpr.EcoLprPlugin
import net.luckperms.api.context.ContextSet
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode

class GroupWrapper(
    plugin: EcoLprPlugin,
    group : Group
) : Wrapper<Group>(plugin, group) {
    val name = group.name
    val displayName = group.displayName

    fun contains(user : UserWrapper): Boolean {
        return user.primaryGroup == name || user.obj.data().toCollection().any {
            it.type == NodeType.INHERITANCE && (it as InheritanceNode).groupName.equals(name, ignoreCase = true)
        }
    }

    fun contains(user : UserWrapper, ctx : ContextSet): Boolean {
        return user.obj.data().toCollection().any {
            it.type == NodeType.INHERITANCE &&
                    it.contexts == ctx &&
                    (it as InheritanceNode).groupName.equals(name, ignoreCase = true)
        }
    }
}