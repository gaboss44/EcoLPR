package com.github.gaboss44.ecolpr.core.util

import com.willfp.eco.core.config.base.LangYml
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.StaticPlaceholder
import net.luckperms.api.context.ContextSet
import net.luckperms.api.context.MutableContextSet
import java.util.Collections

fun Config.selfInject(key: String, keyAsPrefix: Boolean = false): List<InjectablePlaceholder> {
    val section = this.getSubsectionOrNull(key) ?: return emptyList()
    val placeholders = mutableListOf<InjectablePlaceholder>()
    for (subkey: String in section.getKeys(false)) {
        val value = section.getString(subkey)
        placeholders.add(
            StaticPlaceholder(
            (if (keyAsPrefix) "${key}_" else "" ) + subkey
            ) { value }
        )
    }
    this.addInjectablePlaceholder(placeholders)
    return Collections.unmodifiableList(placeholders)
}

fun LangYml.selfInjectPrefix(): InjectablePlaceholder? {
    val prefix = prefix ?: return null
    val placeholder = StaticPlaceholder("prefix") { prefix }
    this.injectPlaceholders(placeholder)
    return placeholder
}

fun parseContextSet(config: Config): ContextSet {
    val mutableContext = MutableContextSet.create()
    config.getKeys(false).forEach { key ->
        config.get(key)?.let { value -> mutableContext.processContextValue(key, value) }
    }
    return mutableContext.immutableCopy()
}

private fun MutableContextSet.processContextValue(
    key: String,
    value: Any
): MutableContextSet {
    when (value) {
        is String -> if (value.isNotEmpty()) {
            add(key, value)
        }
        is List<*> -> value.filterIsInstance<String>()
            .filter { it.isNotEmpty() }
            .forEach { add(key, it) }
    }
    return this
}