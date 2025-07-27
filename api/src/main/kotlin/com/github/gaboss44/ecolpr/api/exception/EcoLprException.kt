package com.github.gaboss44.ecolpr.api.exception

open class EcoLprException : RuntimeException {

    final override val message: String

    // Primary constructor with optional error details
    constructor(
        message: String,
        suppressStackTrace: Boolean = true
    ) : super(
        message,
        null,
        suppressStackTrace,
        !suppressStackTrace
            ) { this.message = message }
    
    // Constructor for cases where we need the cause
    constructor(
        message: String,
        cause: Throwable?,
        suppressStackTrace: Boolean = true
    ) : super(
        message,
        cause,
        suppressStackTrace,
        !suppressStackTrace
            ) { this.message = message }
}
