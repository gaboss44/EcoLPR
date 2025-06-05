package com.github.gaboss44.ecolpr.util

/**
 * Implementation of the OrderedList interface that maintains elements in their insertion order.
 * This implementation uses a map for fast lookups to improve performance.
 *
 * @param T The type of elements in this ordered list
 * @property elements The backing list that stores the elements
 */
internal class OrderedListImpl<T> internal constructor(
    private val elements: List<T>
) : OrderedList<T> {

    override fun getNext(current: T): T? {
        val index = elements.indexOf(current)
        return if (index != -1 && index < elements.lastIndex) elements[index + 1] else null
    }

    override fun getPrevious(current: T): T? {
        val index = elements.indexOf(current)
        return if (index > 0) elements[index - 1] else null
    }

    override fun getPosition(element: T): Int = elements.indexOf(element)

    override fun isLast(element: T): Boolean = elements.isNotEmpty() && elements.indexOf(element) == elements.lastIndex

    override fun isFirst(element: T): Boolean = elements.isNotEmpty() && elements.indexOf(element) == 0

    override fun getAllAfter(element: T): List<T> {
        val index = elements.indexOf(element)
        return if (index != -1) elements.drop(index + 1) else emptyList()
    }

    override fun getAllBefore(element: T): List<T> {
        val index = elements.indexOf(element)
        return if (index != -1) elements.take(index) else emptyList()
    }

    override fun getAt(position: Int): T? = elements.getOrNull(position)

    override fun iterator(): Iterator<T> = elements.iterator()
    override fun contains(element: T): Boolean = elements.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }
    override fun get(index: Int): T {
        if (index < 0 || index >= elements.size) {
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        }
        return elements[index]
    }

    override fun indexOf(element: T): Int {
        return elements.indexOf(element)
    }

    override fun lastIndexOf(element: T): Int {
        return elements.lastIndexOf(element)
    }

    override fun listIterator(): ListIterator<T> {
        return elements.listIterator()
    }

    override fun listIterator(index: Int): ListIterator<T> {
        if (index < 0 || index > elements.size) {
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        }
        return elements.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw IndexOutOfBoundsException("fromIndex: $fromIndex, toIndex: $toIndex, size: $size")
        }
        // Devolvemos una sublista de la lista original
        return elements.subList(fromIndex, toIndex)
    }

    override val size: Int get() = elements.size
    override fun isEmpty(): Boolean = elements.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is List<*>) return false
        
        if (size != other.size) return false
        
        for (i in elements.indices) {
            if (elements[i] != other[i]) return false
        }
        
        return true
    }

    override fun hashCode(): Int = elements.hashCode()

    override fun toString(): String = elements.toString()

    companion object {
        /**
         * Creates an ordered list from the specified collection
         * @param collection The collection to create an ordered list from
         * @return A new ordered list containing the elements from the collection
         */
        fun <T> from(collection: Collection<T>): OrderedList<T> = OrderedListImpl(collection.toList())
        
        /**
         * Creates an ordered list from the specified list
         * @param list The list to create an ordered list from
         * @return A new ordered list containing the elements from the list
         */
        fun <T> from(list: List<T>): OrderedList<T> = OrderedListImpl(list)

        /**
         * Creates an empty ordered list
         * @return A new empty ordered list
         */
        fun <T> empty(): OrderedList<T> = OrderedListImpl(emptyList())
        
        /**
         * Creates an ordered list from the specified vararg elements
         * @param elements The elements to create an ordered list from
         * @return A new ordered list containing the specified elements
         */
        fun <T> of(vararg elements: T): OrderedList<T> = OrderedListImpl(elements.toList())
    }
}
