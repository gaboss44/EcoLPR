package com.github.gaboss44.ecolpr.util

class OrderedList<T> internal constructor(private val elements: List<T>) : List<T> {

    fun getNext(current: T): T? {
        val index = elements.indexOf(current)
        return if (index != -1 && index < elements.lastIndex) elements[index + 1] else null
    }

    fun getPrevious(current: T): T? {
        val index = elements.indexOf(current)
        return if (index > 0) elements[index - 1] else null
    }

    fun getPosition(element: T): Int = elements.indexOf(element)

    fun isLast(element: T): Boolean = elements.isNotEmpty() && elements.indexOf(element) == elements.lastIndex

    fun isFirst(element: T): Boolean = elements.isNotEmpty() && elements.indexOf(element) == 0

    fun getAllAfter(element: T): List<T> {
        val index = elements.indexOf(element)
        return if (index != -1) elements.drop(index + 1) else emptyList()
    }

    fun getAllBefore(element: T): List<T> {
        val index = elements.indexOf(element)
        return if (index != -1) elements.take(index) else emptyList()
    }

    fun getAt(position: Int): T? = elements.getOrNull(position)

    override fun iterator(): Iterator<T> = elements.iterator()
    override fun contains(element: T): Boolean = elements.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }
    override operator fun get(index: Int): T {
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

        fun <T> from(collection: Collection<T>): OrderedList<T> = OrderedList(collection.toList())

        fun <T> from(list: List<T>): OrderedList<T> = OrderedList(list)

        fun <T> empty(): OrderedList<T> = OrderedList(emptyList())

        fun <T> of(vararg elements: T): OrderedList<T> = OrderedList(elements.toList())

    }
}
