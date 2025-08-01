package com.github.gaboss44.ecolpr.api

import org.jetbrains.annotations.ApiStatus

object EcoLprProvider {
    @Volatile
    private var instance: EcoLpr? = null

    @JvmStatic
    fun get(): EcoLpr { return instance ?: throw NotLoadedException() }

    @ApiStatus.Internal
    fun register(api: EcoLpr) { instance = api }

    @ApiStatus.Internal
    fun unregister() { instance = null }

    private const val ERROR_MESSAGE =
        "The EcoLpr API isn't loaded yet!\n" +
                "Possible causes:\n" +
                "  - EcoLpr plugin is not installed or failed to enable\n" +
                "  - Missing plugin dependency declaration\n" +
                "  - API accessed before plugin enable phase\n"

    private class NotLoadedException : IllegalStateException(ERROR_MESSAGE)
}
