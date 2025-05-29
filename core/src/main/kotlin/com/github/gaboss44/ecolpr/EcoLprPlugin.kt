package com.github.gaboss44.ecolpr

import com.willfp.libreforge.loader.LibreforgePlugin
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.plugin.RegisteredServiceProvider
import com.github.gaboss44.ecolpr.wrapper.LuckPermsRepository

internal lateinit var plugin: EcoLprPlugin
    private set

class EcoLprPlugin : LibreforgePlugin() {
    init {
        plugin = this
    }

    lateinit var repo: LuckPermsRepository
    lateinit var ecoLpr: EcoLpr

    override fun handleEnable() {
        val luckperms = getLuckPerms() ?: run {
            logger.severe("Could not get LuckPerms. Disabling...")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }
        repo = LuckPermsRepository(this, luckperms)
    }

    private fun getLuckPerms(): LuckPerms? {
        val provider: RegisteredServiceProvider<LuckPerms>? =
            Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
        return provider?.provider
    }
}
