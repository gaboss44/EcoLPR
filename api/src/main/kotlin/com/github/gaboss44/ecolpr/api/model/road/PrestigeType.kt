package com.github.gaboss44.ecolpr.api.model.road

enum class PrestigeType(val lowerValue: String) {

    EGRESSION("egression"),

    RECURSION("recursion"),

    MIGRATION("migration");

    companion object {
        private val byLowerValue = entries.associateBy { it.lowerValue.lowercase() }

        @JvmStatic
        operator fun get(lowerValue: String?) = lowerValue?.let { byLowerValue[it.lowercase()] }
    }
}