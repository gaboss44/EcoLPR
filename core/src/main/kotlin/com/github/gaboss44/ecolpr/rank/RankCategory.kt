package com.github.gaboss44.ecolpr.rank

import com.github.gaboss44.ecolpr.EcoLprPlugin
import com.github.gaboss44.ecolpr.exception.InvalidConfigurationException
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory

object RankCategory : RegistrableCategory<Rank>("rank", "ranks") {
    private val byIdOverride : MutableMap<String, Rank> = mutableMapOf()

    fun getByIdOverride(id: String): Rank? = byIdOverride[id]

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        plugin as EcoLprPlugin
        try {
            registry.register(RankImpl(id, config, plugin))
        } catch (e: Exception) {
            when (e) {
                is InvalidConfigurationException,
                is IllegalStateException,
                is IllegalArgumentException -> plugin.logger.warning("Failed to load rank '$id': ${e.message}")
                else -> {
                    plugin.logger.severe("An unexpected error occurred while loading rank '$id': ${e.message}")
                    if (plugin.configYml.getBoolOrNull("debug") ?: false) e.printStackTrace()
                }
            }
        }
    }

    override fun afterReload(plugin: LibreforgePlugin) {
        byIdOverride.clear()
        for (rank in registry.values()) {
            byIdOverride[rank.idOverride] = rank
        }
    }
}