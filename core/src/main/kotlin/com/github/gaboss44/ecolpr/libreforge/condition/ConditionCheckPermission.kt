package com.github.gaboss44.ecolpr.libreforge.condition

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.get
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player

class ConditionCheckPermission(private val plugin: EcoLprPlugin) : QueryableCondition("check_permission") {
    override val arguments = arguments {
        require("permission", "You must specify the permission!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: QueryOptions
    ): Boolean {
        val user = plugin.repository.getUser(dispatcher.get<Player>() ?: return false)
        val permission = config.getString("permission")
            .takeIf { it.isNotEmpty() } ?: return false

        return if (compileData.context().isEmpty) user.permissionData.checkPermission(permission).asBoolean()
        else user.getPermissionData(compileData).checkPermission(permission).asBoolean()
    }
}