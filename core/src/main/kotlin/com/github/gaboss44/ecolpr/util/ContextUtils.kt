package com.github.gaboss44.ecolpr.util

import net.luckperms.api.context.ImmutableContextSet

object ContextUtils {
    @JvmStatic
    fun context(vararg strings: String): ImmutableContextSet =
        buildContextSet(strings.asIterable())

    @JvmStatic
    fun context(list: List<String>): ImmutableContextSet =
        buildContextSet(list)

    private fun buildContextSet(entries: Iterable<String>): ImmutableContextSet =
        ImmutableContextSet.builder().apply {
            for (entry in entries) {
                val (key, value) = entry.split("=", limit = 2).also {
                    require(it.size == 2 && it[0].isNotBlank() && it[1].isNotBlank()) {
                        "Each context string must follow the format 'key=value' and both substrings must not be blank: '$entry'"
                    }
                }
                add(key, value)
            }
        }.build()
}