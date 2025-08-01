package com.github.gaboss44.ecolpr.core.libreforge.condition

object ConditionPrestigeLevelLowerThan : PrestigeLevelCondition("ecolpr_prestige_level_lower_than") {
    override fun compare(level: Int, value: Int): Boolean {
        return level < value
    }
}