package com.github.gaboss44.ecolpr.api.util

@FunctionalInterface
interface Result {

    fun wasSuccessful() : Boolean

    enum class Generic(var success: Boolean) : Result {

        SUCCESS(true),

        FAILURE(false);

        override fun wasSuccessful() = success
    }
}