package com.github.gaboss44.ecolpr.core.libreforge.filter

object FilterPrestigeLevelEquals : PrestigeLevelFilter("ecolpr_prestige_level_equals") {
    override fun compare(level: Int, value: Int): Boolean {
        return level == value
    }
}