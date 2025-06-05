package com.github.gaboss44.ecolpr.util

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.separatorAmbivalent
import net.luckperms.api.context.ContextSet
import net.luckperms.api.context.ImmutableContextSet
import net.luckperms.api.context.MutableContextSet
import net.luckperms.api.query.Flag
import net.luckperms.api.query.QueryMode
import net.luckperms.api.query.QueryOptions

internal fun options(ctx: ContextSet): QueryOptions =
    QueryOptions.contextual(ctx)

internal val Config.queryOptions: QueryOptions get() =
    this.separatorAmbivalent().let { config ->
        val ctxSet = config.getSubsectionOrNull("context").contextSet
        val builder = when {
            ctxSet == null || ctxSet.isEmpty -> QueryOptions.builder(QueryMode.NON_CONTEXTUAL)
            else -> QueryOptions.builder(QueryMode.CONTEXTUAL).context(ctxSet)
        }
        config.getSubsectionOrNull("flags")?.let { builder.flags(it) } ?: builder
    } .build()

private val Config?.contextSet: ImmutableContextSet? get() =
    this?.let { config ->
        val mutableContext = MutableContextSet.create()
        config.getKeys(false).forEach { key ->
            config.get(key)?.let { value ->
                mutableContext.processContextValue(key, value)
            }
        }
        mutableContext.immutableCopy()
    }

private fun MutableContextSet.processContextValue(key: String, value: Any): MutableContextSet {
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

private fun QueryOptions.Builder.flags(cfg: Config): QueryOptions.Builder {
    cfg.getKeys(false).forEach { key ->
        val value = cfg.getBoolOrNull(key) ?: true
        when (key.lowercase()) {
            "resolve_inheritance" -> flag(Flag.RESOLVE_INHERITANCE, value)
            "include_nodes_without_server_context" -> flag(Flag.INCLUDE_NODES_WITHOUT_SERVER_CONTEXT, value)
            "include_nodes_without_world_context" -> flag(Flag.INCLUDE_NODES_WITHOUT_WORLD_CONTEXT, value)
            "apply_inheritance_nodes_without_server_context" -> flag(Flag.APPLY_INHERITANCE_NODES_WITHOUT_SERVER_CONTEXT, value)
            "apply_inheritance_nodes_without_world_context" -> flag(Flag.APPLY_INHERITANCE_NODES_WITHOUT_WORLD_CONTEXT, value)
        }
    }
    return this
}