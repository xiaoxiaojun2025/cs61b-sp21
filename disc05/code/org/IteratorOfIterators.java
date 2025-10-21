package org;

import java.util.*;
public class IteratorOfIterators implements Iterator<Integer> {
    private List<Iterator<Integer>> list;
    public IteratorOfIterators(List<Iterator<Integer>> a) {
        list = a;
    }
    @Override
    public boolean hasNext() {
        return !list.isEmpty();
    }
    @Override
    public Integer next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Iterator<Integer> iterator = list.removeFirst();
        Integer res = iterator.next();
        if (iterator.hasNext()) {
            list.addLast(iterator);
        }
        return res;
    }
}
