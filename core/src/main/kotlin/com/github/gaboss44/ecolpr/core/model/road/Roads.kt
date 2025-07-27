package com.github.gaboss44.ecolpr.core.model.road

import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.exception.InvalidConfigException
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory

object Roads : RegistrableCategory<Road>("road", "roads") {

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        plugin as EcoLprPlugin
        try { registry.register(Road(plugin, id, config)) }
        catch (e: Exception) {
            when (e) {
                is InvalidConfigException,
                is IllegalStateException,
                is IllegalArgumentException -> plugin.logger.warning("Failed to load road '$id': ${e.message}")
                else -> {
                    plugin.logger.severe("An unexpected error occurred while loading road '$id': ${e.message}")
                    if (plugin.isDebug) e.printStackTrace()
                }
            }
        }
    }

    fun proxyValues() = registry.values().mapNotNull { it.proxy }.toSet()

}