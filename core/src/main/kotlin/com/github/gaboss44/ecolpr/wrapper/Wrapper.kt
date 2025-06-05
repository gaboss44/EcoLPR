package com.github.gaboss44.ecolpr.wrapper

import com.github.gaboss44.ecolpr.EcoLprPlugin

abstract class Wrapper<T>(
    protected val plugin : EcoLprPlugin,
    internal val obj : T
) {
    /**
     * Returns the wrapped object as a String.
     */
    override fun toString(): String {
        return obj.toString()
    }

    /**
     * Returns the hash code of the wrapped object.
     */
    override fun hashCode(): Int {
        return obj.hashCode()
    }

    /**
     * Checks if the wrapped object is equal to another object.
     */
    override fun equals(other: Any?): Boolean {
        return obj == other
    }
}