package com.github.gaboss44.ecolpr.core.util

enum class MatchMode(val lowerValue: String) {
    ANY("any"),
    ALL("all");

    fun <T> evaluate(iterable: Iterable<T>, predicate: (T) -> Boolean): Boolean {
        return when (this) {
            ANY -> iterable.any(predicate)
            ALL -> iterable.all(predicate)
        }
    }

    companion object {
        private val byLowerValue = entries.associateBy { it.lowerValue }

        operator fun get(lowerValue: String?) = byLowerValue[lowerValue?.lowercase()]
    }
}