package com.github.gaboss44.ecolpr.api.util

interface Data<T : Data<T>> {

    val parents: List<T>

    private fun inherits(
        other: Data<T>,
        visited: MutableList<Data<T>>
    ): Boolean {
        if (this == other) return true
        if (!visited.add(this)) return false
        return parents.any { it.inherits(other, visited) }
    }

    fun inherits(
        other: Data<T>
    ) = inherits(
        other,
        mutableListOf()
    )

    fun isCustom() = false

    class Custom<T : Data<T>> internal constructor(
        override val parents: List<T>
    ): Data<T> { override fun isCustom() = true }

    companion object {

        @JvmStatic
        fun <T : Data<T>> of(parents: List<T>) = Custom(parents.toList())

        @JvmStatic
        fun <T : Data<T>> of(vararg parents: T) = Custom(parents.toList())

    }

}
