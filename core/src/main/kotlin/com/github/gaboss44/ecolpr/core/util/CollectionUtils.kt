package com.github.gaboss44.ecolpr.core.util

/**
 * Returns the next element in the list after the given [element].
 * Throws [NoSuchElementException] if the element is the last or not found.
 */
fun <T> List<T>.next(element: T): T =
    nextOrNull(element)
        ?: throw NoSuchElementException("No next element found after: $element")

/**
 * Returns the previous element in the list before the given [element].
 * Throws [NoSuchElementException] if the element is the first or not found.
 */
fun <T> List<T>.previous(element: T): T =
    previousOrNull(element)
        ?: throw NoSuchElementException("No previous element found before: $element")

/**
 * Returns the next element in the list after the given [element], or `null` if it does not exist.
 */
fun <T> List<T>.nextOrNull(element: T): T? {
    val index = indexOf(element)
    return if (index in 0 until lastIndex) this[index + 1] else null
}

/**
 * Returns the previous element in the list before the given [element], or `null` if it does not exist.
 */
fun <T> List<T>.previousOrNull(element: T): T? {
    val index = indexOf(element)
    return if (index > 0) this[index - 1] else null
}
