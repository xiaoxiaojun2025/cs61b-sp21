package deque;

import java.util.Iterator;

public interface Deque<T> {
    /** return the size of the Deque. */
    int size();
    /** print all elements of the Deque. */
    void printDeque();
    /** add one element to the last of the Deque. */
    void addLast(T i);
    /** add one element to the front of the Deque. */
    void addFirst(T i);
    /** remove the back of the Deque. */
    T removeLast();
    /** remove the front of the Deque. */
    T removeFirst();
    /** return the element of the given index. */
    T get(int index);
    /** tell if the Deque is empty. */
    default boolean isEmpty() {
        return size() == 0;
    }
}
