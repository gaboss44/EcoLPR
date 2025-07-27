package com.github.gaboss44.ecolpr.api.model.road

enum class PrestigeType(val lowerValue: String) {

    EGRESS("egress"),

    RECURSE("recurse"),

    MIGRATE("migrate");

    companion object {
        private val byLowerValue = entries.associateBy { it.lowerValue.lowercase() }

        @JvmStatic
        fun getByLowerValue(lowerValue: String) = byLowerValue[lowerValue.lowercase()]
    }
}