package com.github.gaboss44.ecolpr.core.libreforge.condition

object ConditionPrestigeLevelAtMost : PrestigeLevelCondition("ecolpr_prestige_level_at_most") {
    override fun compare(level: Int, value: Int): Boolean {
        return level <= value
    }
}