package com.github.gaboss44.ecolpr.core

object EcoLprSettings {

    private var debug: Boolean = false

    fun isDebug() = debug

    internal fun setDebug(value: Boolean) { debug = value }
}