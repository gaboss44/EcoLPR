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

/**
 * Returns the element after [element] in the list, or the result of [ifLast] if [element] is the last.
 *
 * @throws NoSuchElementException if [element] is not found in the list.
 */
inline fun <T> List<T>.nextOrIfLast(
    element: T,
    ifLast: () -> T
): T {
    val index = indexOf(element)
    if (index == -1) {
        throw NoSuchElementException("Element '$element' not found in the list.")
    }
    return if (index < lastIndex) this[index + 1] else ifLast()
}

/**
 * Returns the element before [element] in the list, or the result of [ifFirst] if [element] is the first.
 *
 * @throws NoSuchElementException if [element] is not found in the list.
 */
inline fun <T> List<T>.previousOrIfFirst(
    element: T,
    ifFirst: () -> T
): T {
    val index = indexOf(element)
    if (index == -1) {
        throw NoSuchElementException("Element '$element' not found in the list.")
    }
    return if (index > 0) this[index - 1] else ifFirst()
}

/**
 * Returns the next element in the list cyclically after the given [element],
 * or `null` if the list is empty or the element is not found.
 */
fun <T> List<T>.nextCyclicOrNull(element: T): T? {
    if (isEmpty()) return null
    val index = indexOf(element)
    return if (index == -1) null else this[(index + 1) % size]
}

/**
 * Returns the previous element in the list cyclically before the given [element],
 * or `null` if the list is empty or the element is not found.
 */
fun <T> List<T>.previousCyclicOrNull(element: T): T? {
    if (isEmpty()) return null
    val index = indexOf(element)
    return if (index == -1) null else this[(index - 1 + size) % size]
}

/**
 * Returns a new map with the keys grouped by the value returned by the selector.
 */
inline fun <K, V, R> Map<K, V>.groupKeysBy(
    selector: (Map.Entry<K, V>) -> R
): Map<R, List<K>> =
    entries.groupBy(selector).mapValues { it.value.map { entry -> entry.key } }

/**
 * Returns a list with pairs of consecutive elements (sliding pairs).
 */
fun <T> List<T>.zipWithNextPair(): List<Pair<T, T>> =
    zipWithNext()

/**
 * Returns the list of unique elements, keeping the last repeated element (reverse of distinct).
 */
fun <T> List<T>.distinctByLast(): List<T> =
    asReversed().distinct().asReversed()

inline fun <T, R> Collection<T>.ifEmpty(
    block: (Collection<T>) -> R
): R? = if (isEmpty()) block(this) else null

/**
 * Executes a block if the collection is not empty.
 */
inline fun <T, R> Collection<T>.ifNotEmpty(
    block: (Collection<T>) -> R
): R? = if (isNotEmpty()) block(this) else null

/**
 * Returns true if the element is the first in the list.
 */
fun <T> List<T>.isFirst(element: T): Boolean =
    !isEmpty() && indexOf(element) == 0

/**
 * Returns true if the element is the last in the list.
 */
fun <T> List<T>.isLast(element: T): Boolean =
    !isEmpty() && indexOf(element) == lastIndex

/**
 * Executes a block if the collection is not empty, always returns the same list.
 */
inline fun <T> List<T>.runIfNotEmpty(action: (List<T>) -> Unit): List<T> {
    if (isNotEmpty()) action(this)
    return this
}

/**
 * Executes a block if the collection is empty, always returns the same list.
 */
inline fun <T> List<T>.runIfEmpty(action: (List<T>) -> Unit): List<T> {
    if (isEmpty()) action(this)
    return this
}

