package com.github.gaboss44.ecolpr.util

internal class OrderedSetImpl<T> internal constructor(
    private val elements: List<T>
) : OrderedSet<T> {
    private val elementToIndex: Map<T, Int> = elements.withIndex().associate { it.value to it.index }

    override fun getNext(current: T): T? = elementToIndex[current]?.let { index ->
        if (index < elements.lastIndex) elements[index + 1] else null
    }

    override fun getPrevious(current: T): T? = elementToIndex[current]?.let { index ->
        if (index > 0) elements[index - 1] else null
    }

    override fun getPosition(element: T): Int = elementToIndex[element] ?: -1

    override fun isLast(element: T): Boolean = elementToIndex[element] == elements.lastIndex

    override fun isFirst(element: T): Boolean = elementToIndex[element] == 0

    override fun getAllAfter(element: T): List<T> = elementToIndex[element]?.let {
        elements.drop(it + 1)
    } ?: emptyList()

    override fun getAllBefore(element: T): List<T> = elementToIndex[element]?.let {
        elements.take(it)
    } ?: emptyList()

    override fun getAt(position: Int): T? = elements.getOrNull(position)

    override fun toList(): List<T> = elements.toList()

    override fun iterator(): Iterator<T> = elements.iterator()
    override fun contains(element: T): Boolean = elementToIndex.containsKey(element)
    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }
    override val size: Int get() = elements.size
    override fun isEmpty(): Boolean = elements.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Set<*>) return false
        return size == other.size && try {
            @Suppress("UNCHECKED_CAST")
            containsAll(other as Set<T>)
        } catch (_: ClassCastException) { false }
    }

    override fun hashCode(): Int = elements.hashCode()

    override fun toString(): String = elements.toString()

    companion object {
        /**
         * Creates an ordered set from the specified collection
         * @param collection The collection to create an ordered set from
         * @return A new ordered set containing the elements from the collection
         */
        fun <T> from(collection: Collection<T>): OrderedSet<T> = OrderedSetImpl(collection.distinct())

        /**
         * Creates an empty ordered set
         * @return A new empty ordered set
         */
        fun <T> empty(): OrderedSet<T> = OrderedSetImpl(emptyList())
    }
}