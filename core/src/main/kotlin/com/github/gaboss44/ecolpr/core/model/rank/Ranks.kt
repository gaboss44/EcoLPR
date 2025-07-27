package com.github.gaboss44.ecolpr.core.model.rank

import com.github.gaboss44.ecolpr.core.EcoLprPlugin
import com.github.gaboss44.ecolpr.core.exception.InvalidConfigException
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory
import net.luckperms.api.model.group.Group

object Ranks : RegistrableCategory<Rank>("rank", "ranks") {

    internal val byGroup = mutableMapOf<String, Rank>()

    fun getByGroup(group: Group) = getByGroup(group.name)

    fun getByGroup(group: String) = byGroup[group]

    override fun clear(plugin: LibreforgePlugin) { registry.clear() }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        plugin as EcoLprPlugin
        try {
            registry.register(Rank(plugin, id, config))
        } catch (e: Exception) {
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
