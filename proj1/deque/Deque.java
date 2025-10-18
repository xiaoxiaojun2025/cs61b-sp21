package deque;

public interface Deque<Item> {
    /** return the size of the Deque. */
    int size();
    /** print all elements of the Deque. */
    void printDeque();
    /** add one element to the last of the Deque. */
    void addLast(Item i);
    /** add one element to the front of the Deque. */
    void addFirst(Item i);
    /** remove the back of the Deque. */
    Item removeLast();
    /** remove the front of the Deque. */
    Item removeFirst();
    /** return the element of the given index. */
    Item get(int index);
    /** tell if the Deque is empty. */
    default boolean isEmpty() {
        return size() == 0;
    }
}
