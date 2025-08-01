package com.github.gaboss44.ecolpr.core.libreforge.condition

object ConditionPrestigeLevelEquals : PrestigeLevelCondition("ecolpr_prestige_level_equals") {
    override fun compare(level: Int, value: Int): Boolean {
        return level == value
    }
}