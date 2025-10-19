package deque;

import java.util.Iterator;

/** Deque implemented by Array
 * Invariants:
 * 1.front should be the previous of the first element.
 * 2.rear should be exactly the last element.
 * 3.size should be the size of Deque,the same as capacity.
 */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int front;
    private int rear;
    private int capacity;
    public static final double EXPANSION_FACTOR = 2.0;
    public static final double REDUCTION_FACTOR = 0.5;
    public static final double GOOD_COEF = 0.25;
    private static int minCapacity = 8;
    /** Init Deque. */
    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[minCapacity];
        front = 0;
        rear = 0;
        capacity = 8;
    }
    /** Return the size of the Deque. */
    @Override
    public int size() {
        return size;
    }
    /** Return the capacity of the Deque. */
    public int capacity() {
        return capacity;
    }
    /** Get the element of the given index. */
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(front + index + 1) % capacity];
    }
    /** Print all elements of the Deque from the front to the rear. */
    @Override
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
        newCapacity = Math.max(minCapacity, newCapacity);
        T[] temp = (T[]) new Object[newCapacity];
        System.arraycopy(getArray(), 0, temp, 0, size);
        capacity = newCapacity;
        front = capacity - 1;
        rear = (size - 1 + capacity) % capacity;
        items = temp;
    }
    /** Helper of resize,which get an array of Deque from front to rear. */
    private T[] getArray() {
        T[] res = (T[]) new Object[size];
        int begin = (front + 1) % capacity;
        for (int i = 0; i < size; i += 1) {
            res[i] = items[begin];
            begin = (begin + 1) % capacity;
        }
        return res;
    }
    /** Add an element to the front of the Deque. */
    @Override
    public void addFirst(T i) {
        if (size == capacity) {
            resize((int) (capacity * EXPANSION_FACTOR));
        }
        size += 1;
        items[front] = i;
        front = (front - 1 + capacity) % capacity;
    }
    /** Add an element to the back of the Deque. */
    @Override
    public void addLast(T i) {
        if (size == capacity) {
            resize((int) (capacity * EXPANSION_FACTOR));
        }
        size += 1;
        rear = (rear + 1) % capacity;
        items[rear] = i;
    }
    /** Remove the first element of the Deque and return its value. */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        front = (front + 1) % capacity;
        T res = items[front];
        size -= 1;
        if (size / (double) capacity < GOOD_COEF) {
            resize((int) (capacity * REDUCTION_FACTOR));
        }
        return res;
    }
    /** Remove the last element of the Deque and return its value. */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T res = items[rear];
        rear = (rear - 1 + capacity) % capacity;
        size -= 1;
        if (size / (double) capacity < GOOD_COEF) {
            resize((int) (capacity * REDUCTION_FACTOR));
        }
        return res;
    }
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || !(other instanceof Deque)) {
            return false;
        }
        Deque<?> o = (Deque<?>) other;
        if (size != o.size()) {
            return false;
        }
        Iterator<T> i1 = iterator();
        Iterator<?> i2 = o.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            if (!i1.next().equals(i2.next())) {
                return false;
            }
        }
        return true;
    }
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder res = new StringBuilder("[");
        for (int i = 0; i < size - 1; i += 1) {
            res.append(get(i));
            res.append(", ");
        }
        res.append(get(size - 1));
        res.append("]");
        return res.toString();
    }
    /** return an iterator. */
    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    private class ArrayDequeIterator implements Iterator<T> {
        private int pos;
        ArrayDequeIterator() {
            pos = 0;
        }
        @Override
        public boolean hasNext() {
            return pos < size;
        }
        @Override
        public T next() {
            T res = get(pos);
            pos += 1;
            return res;
        }
    }
}
