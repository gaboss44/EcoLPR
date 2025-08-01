package com.github.gaboss44.ecolpr.core.libreforge.condition

object ConditionPrestigeLevelAtLeast : PrestigeLevelCondition("ecolpr_prestige_level_at_least") {
    override fun compare(level: Int, value: Int): Boolean {
        return level >= value
    }
}