package deque;
/** Deque implemented by Array
 * Invariants:
 * 1.front should be the previous of the first element.
 * 2.rear should be exactly the last element.
 * 3.size should be the size of Deque,the same as capacity.
 */
public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int front;
    private int rear;
    private int capacity;
    public static final double EXPANSION_FACTOR = 2.0;
    public static final double REDUCTION_FACTOR = 0.5;
    public static final double GOOD_COEF = 0.25;
    /** Init Deque. */
    public ArrayDeque() {
        size = 0;
        items = (Item[]) new Object[8];
        front = 0;
        rear = 0;
        capacity = 8;
    }
    /** Return the size of the Deque. */
    public int size() {
        return size;
    }
    /** Return the capacity of the Deque. */
    public int capacity() {
        return capacity;
    }
    /** check if Deque is empty. */
    public boolean isEmpty() {
        return size == 0;
    }
    /** Get the element of the given index. */
    public Item get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(front + index + 1) % capacity];
    }
    /** Print all elements of the Deque from the front to the rear. */
    public void printDeque() {
        int begin = (front + 1) % capacity;
        for (int i = 0; i < size; i += 1) {
            System.out.print(items[begin] + " ");
            begin = (begin + 1) % capacity;
        }
        System.out.println();
    }
    /** Resize the capacity of the Deque. */
    private void resize(int newCapacity) {
        Item[] temp = (Item[]) new Object[newCapacity];
        System.arraycopy(getArray(), 0, temp, 0, size);
        capacity = newCapacity;
        front = capacity - 1;
        rear = size - 1;
        items = temp;
    }
    /** Helper of resize,which get an array of Deque from front to rear. */
    private Item[] getArray() {
        Item[] res = (Item[]) new Object[size];
        int begin = (front + 1) % capacity;
        for (int i = 0; i < size; i += 1) {
            res[i] = items[begin];
            begin = (begin + 1) % capacity;
        }
        return res;
    }
    /** Add an element to the front of the Deque. */
    public void addFirst(Item i) {
        if (size == capacity) {
            resize((int)(capacity * EXPANSION_FACTOR));
        }
        size += 1;
        items[front] = i;
        front = (front - 1 + capacity) % capacity;
    }
    /** Add an element to the back of the Deque. */
    public void addLast(Item i) {
        if (size == capacity) {
            resize((int)(capacity * EXPANSION_FACTOR));
        }
        size += 1;
        rear = (rear + 1) % capacity;
        items[rear] = i;
    }
    /** Remove the first element of the Deque and return its value. */
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }
        front = (front + 1) % capacity;
        Item res = items[front];
        size -= 1;
        if (size / (double)capacity < GOOD_COEF) {
            resize((int)(capacity * REDUCTION_FACTOR));
        }
        return res;
    }
    /** Remove the last element of the Deque and return its value. */
    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }
        Item res = items[rear];
        rear = (rear - 1 + capacity) % capacity;
        size -= 1;
        if (size / (double)capacity < GOOD_COEF) {
            resize((int)(capacity * REDUCTION_FACTOR));
        }
        return res;
    }
}
