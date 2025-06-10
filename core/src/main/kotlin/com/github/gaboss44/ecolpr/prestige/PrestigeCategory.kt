package com.github.gaboss44.ecolpr.prestige

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.exception.InvalidConfigurationException
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory

object PrestigeCategory : RegistrableCategory<Prestige>("prestige", "prestige") {
    private val byIdOverride : MutableMap<String, Prestige> = mutableMapOf()

    fun getByIdOverride(id: String): Prestige? = byIdOverride[id]

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        plugin as EcoLprPlugin
        try {
            registry.register(PrestigeImpl(id, config, plugin))
        } catch (e: Exception) {
            when (e) {
                is InvalidConfigurationException,
                is IllegalStateException,
                is IllegalArgumentException -> plugin.logger.warning("Failed to load prestige '$id': ${e.message}")
                else -> {
                    plugin.logger.severe("An unexpected error occurred while loading prestige '$id': ${e.message}")
                    if (plugin.configYml.getBoolOrNull("debug") ?: false) e.printStackTrace()
                }
            }
        }
    }

    override fun afterReload(plugin: LibreforgePlugin) {
        byIdOverride.clear()
        for (prestige in registry.values()) {
            byIdOverride[prestige.idOverride] = prestige
        }
    }
}