package com.github.gaboss44.ecolpr.core.libreforge.condition

object ConditionPrestigeLevelGreaterThan : PrestigeLevelCondition("ecolpr_prestige_level_greater_than") {
    override fun compare(level: Int, value: Int): Boolean {
        return level > value
    }
}