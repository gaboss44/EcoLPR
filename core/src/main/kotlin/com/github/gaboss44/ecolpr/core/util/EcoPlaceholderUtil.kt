package com.github.gaboss44.ecolpr.core.util

import com.google.common.collect.ImmutableSet
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.Placeholder
import java.lang.reflect.Field

object EcoPlaceholderUtil {
    private val registeredPlaceholdersField: Field by lazy {
        val field = PlaceholderManager::class.java.getDeclaredField("REGISTERED_PLACEHOLDERS")
        field.isAccessible = true
        field
    }

    @Suppress("UNCHECKED_CAST")
    private fun getRegisteredMap() = registeredPlaceholdersField.get(null) as MutableMap<EcoPlugin, MutableSet<Placeholder>>

    fun unregister(
        plugin: EcoPlugin,
        vararg placeholders: Placeholder
    ) = unregister(
        plugin,
        placeholders.asIterable()
    )

    fun unregister(plugin: EcoPlugin, placeholders: Iterable<Placeholder>) {
        val map = getRegisteredMap()

        val current = map[plugin] ?: return

        val mutable = current.toMutableSet()

        for (placeholder in placeholders) mutable.removeIf { it.pattern == placeholder.pattern }

        map[plugin] = ImmutableSet.copyOf(mutable)
    }
}