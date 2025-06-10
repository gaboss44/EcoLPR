package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.get
import com.willfp.libreforge.getStrings
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player

class ConditionCheckPermission(private val plugin: EcoLprPlugin) : QueryableCondition("check_permission") {
    override val arguments = arguments {
        require(listOf("permissions", "permission"), "You must specify the permission(s)!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: QueryOptions?
    ): Boolean {
        val user = plugin.repository.getUser(dispatcher.get<Player>() ?: return false)
        val permissions = config.getStrings("permissions", "permission")
            .takeIf { it.isNotEmpty() } ?: return false

        return if (compileData == null || compileData.context().isEmpty) {
            permissions.all { user.permissionData.checkPermission(it).asBoolean() }
        } else {
            permissions.all { user.getPermissionData(compileData).checkPermission(it).asBoolean() }
        }
    }
}