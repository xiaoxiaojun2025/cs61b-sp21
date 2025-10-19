package deque;

import java.util.Iterator;

/** Deque done with LinkedList */
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private DNode sentibel;
    private int size;
    /** Node of the Deque*/
    private class DNode {
        private T item;
        private DNode prev;
        private DNode next;
        DNode(T i, DNode pr, DNode ne) {
            item = i;
            prev = pr;
            next = ne;
        }
    }
    /** Init Deque. */
    public LinkedListDeque() {
        sentibel = new DNode(null, null, null);
        sentibel.next = sentibel;
        sentibel.prev = sentibel;
        size = 0;
    }
    /**Add one element to the front of the Dqueue. */
    @Override
    public void addFirst(T i) {
        size += 1;
        DNode toBeAdded = new DNode(i, sentibel, sentibel.next);
        sentibel.next.prev = toBeAdded;
        sentibel.next = toBeAdded;
    }
    /**Return the size of the Dqueue */
    @Override
    public int size() {
        return size;
    }
    /**Add one element to the back of the Dqueue. */
    public void addLast(T i) {
        size += 1;
        DNode toBeAdded = new DNode(i, sentibel.prev, sentibel);
        sentibel.prev.next = toBeAdded;
        sentibel.prev = toBeAdded;
    }
    /**Remove the first element of the Deque and
     * return its value and return null if Deque is empty.
     * */
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        T res = sentibel.next.item;
        sentibel.next = sentibel.next.next;
        sentibel.next.prev = sentibel;
        return res;
    }
    /** Remove the last element of the Deque and return its value
     *and return null if the Deque is empty.
     *
     */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        T res = sentibel.prev.item;
        sentibel.prev = sentibel.prev.prev;
        sentibel.prev.next = sentibel;
        return res;
    }
    /**Print all elements of the Deque from the front to the back. */
    @Override
    public void printDeque() {
        DNode p = sentibel.next;
        while (p != null && p != sentibel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }
    /** Get element of the given index.(non-recursive) */
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        DNode p = sentibel.next;
        for (int i = 0; i < index; i += 1) {
            p = p.next;
        }
        return p.item;
    }
    /** Get element of the given index.(recursive) */
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(index, sentibel.next);
    }
    /** Help getRecursive. */
    private T getRecursiveHelper(int index, DNode p) {
        if (index == 0) {
            return p.item;
        }
        return getRecursiveHelper(index - 1, p.next);
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
        int i1 = 0;
        int i2 = 0;
        while (i1 < size && i2 < o.size()) {
            if (!get(i1).equals(o.get(i2))) {
                return false;
            }
            i1 += 1;
            i2 += 1;
        }
        return true;
    }

    /** return an iterator. */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
    private class LinkedListDequeIterator implements Iterator<T> {
        private int pos;
        LinkedListDequeIterator() {
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
