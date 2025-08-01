package com.github.gaboss44.ecolpr.core.libreforge.filter

object FilterPrestigeLevelAtLeast : PrestigeLevelFilter("ecolpr_prestige_level_at_least") {
    override fun compare(level: Int, value: Int): Boolean {
        return level >= value
    }
}