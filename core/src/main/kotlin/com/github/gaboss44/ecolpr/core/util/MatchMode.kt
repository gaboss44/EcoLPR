package com.github.gaboss44.ecolpr.core.util

enum class MatchMode(val lowerValue: String) {

    ANY("any"),

    ALL("all"),

    NONE("none");

    fun <T> evaluate(
        iterable: Iterable<T>,
        predicate: (T) -> Boolean
    ) = when (this) {
        ANY -> iterable.any(predicate)
        ALL -> iterable.all(predicate)
        NONE -> iterable.none(predicate)
    }

    companion object {
        private val byLowerValue = entries.associateBy { it.lowerValue }

        operator fun get(lowerValue: String?) = byLowerValue[lowerValue?.lowercase()]
    }
}
