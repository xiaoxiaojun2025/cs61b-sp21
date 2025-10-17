package deque;

import afu.org.checkerframework.checker.igj.qual.I;

import java.util.Comparator;

public class MaxArrayDeque<Item> {
    /** reuse arraydeque. */
    private ArrayDeque<Item> deque;
    private Comparator<Item> cmpMethod;
    private MaxArrayDeque() {
        deque = new ArrayDeque<>();
        cmpMethod = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return 0;
            }
        };
    }
    public MaxArrayDeque(Comparator<Item> c) {
        deque = new ArrayDeque<>();
        cmpMethod = c;
    }
    public Item max() {
        if (isEmpty()) {
            return null;
        }
        Item res = deque.get(0);
        for (int i = 1; i < deque.size(); i += 1) {
            Item curr = deque.get(i);
            if (cmpMethod.compare(curr, res) > 0) {
                res = curr;
            }
        }
        return res;
    }
    public Item max(Comparator<Item> c) {
        if (isEmpty()) {
            return null;
        }
        Item res = deque.get(0);
        for (int i = 1; i < deque.size(); i += 1) {
            Item curr = deque.get(0);
            if (c.compare(curr, res) > 0) {
                res = curr;
            }
        }
        return res;
    }
    public int size() {
        return deque.size();
    }
    public int capacity() {
        return deque.capacity();
    }
    public boolean isEmpty() {
        return deque.isEmpty();
    }
    public void addFirst(Item i) {
        deque.addFirst(i);
    }
    public void addLast(Item i) {
        deque.addLast(i);
    }
    public Item removeFirst() {
        return deque.removeFirst();
    }
    public Item removeLast() {
        return deque.removeLast();
    }
    public Item get(int index) {
        return deque.get(index);
    }
    public void printDeque() {
        deque.printDeque();
    }

}
