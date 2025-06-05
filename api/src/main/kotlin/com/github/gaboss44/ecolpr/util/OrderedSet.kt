package com.github.gaboss44.ecolpr.util

/**
 * An ordered set interface that maintains insertion order while providing
 * the functionality of a Set. This interface provides methods to navigate through
 * elements in their defined order.
 *
 * @param T The type of elements in this ordered set
 */
interface OrderedSet<T> : Set<T> {
    /**
     * Gets the next element after the specified element
     * @param current The element to find the next element after
     * @return The next element, or null if the current element is the last or not in the set
     */
    fun getNext(current: T): T?
    
    /**
     * Gets the previous element before the specified element
     * @param current The element to find the previous element before
     * @return The previous element, or null if the current element is the first or not in the set
     */
    fun getPrevious(current: T): T?
    
    /**
     * Gets the position of the specified element in the ordered set
     * @param element The element to find the position of
     * @return The position of the element, or -1 if the element is not in the set
     */
    fun getPosition(element: T): Int
    
    /**
     * Checks if the specified element is the last element in the ordered set
     * @param element The element to check
     * @return true if the element is the last element, false otherwise
     */
    fun isLast(element: T): Boolean
    
    /**
     * Checks if the specified element is the first element in the ordered set
     * @param element The element to check
     * @return true if the element is the first element, false otherwise
     */
    fun isFirst(element: T): Boolean
    
    /**
     * Gets all elements after the specified element
     * @param element The element to find elements after
     * @return A list of all elements after the specified element, or an empty list if the element is not in the set
     */
    fun getAllAfter(element: T): List<T>
    
    /**
     * Gets all elements before the specified element
     * @param element The element to find elements before
     * @return A list of all elements before the specified element, or an empty list if the element is not in the set
     */
    fun getAllBefore(element: T): List<T>
    
    /**
     * Gets the element at the specified position
     * @param position The position to get the element at
     * @return The element at the specified position, or null if the position is out of bounds
     */
    fun getAt(position: Int): T?
    
    /**
     * Converts this ordered set to a list, preserving the order
     * @return A list containing all elements in this ordered set in their defined order
     */
    fun toList(): List<T>
}