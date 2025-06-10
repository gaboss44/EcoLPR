package com.github.gaboss44.ecolpr.road

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.exception.InvalidConfigurationException
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory

object RoadCategory : RegistrableCategory<Road>("road", "roads") {
    private val byIdOverride : MutableMap<String, Road> = mutableMapOf()

    fun getByIdOverride(id: String): Road? = byIdOverride[id]

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        plugin as EcoLprPlugin
        try {
            registry.register(RoadImpl(id, config, plugin))
        } catch (e: Exception) {
            when (e) {
                is InvalidConfigurationException,
                is IllegalStateException,
                is IllegalArgumentException -> plugin.logger.warning("Failed to load road '$id': ${e.message}")
                else -> {
                    plugin.logger.severe("An unexpected error occurred while loading road '$id': ${e.message}")
                    if (plugin.configYml.getBoolOrNull("debug") ?: false) e.printStackTrace()
                }
            }
        }
    }

    override fun afterReload(plugin: LibreforgePlugin) {
        byIdOverride.clear()
        for (road in registry.values()) {
            byIdOverride[road.idOverride] = road
        }
    }
}