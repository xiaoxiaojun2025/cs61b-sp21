package org;

import java.util.*;
import java.util.function.Predicate;

public class FilteredList<T> implements Iterable<T> {
    private List<T> L;
    private Predicate<T> filter;
    public FilteredList(List<T> L, Predicate<T> filter) {
        this.L = L;
        this.filter = filter;
    }

    @Override
    public Iterator<T> iterator() {
        return new FilteredListIterator();
    }
    private class FilteredListIterator implements Iterator<T> {
        private int curr;
        FilteredListIterator() {
            curr = 0;
        }
        @Override
        public boolean hasNext() {
            while (curr < L.size() && !filter.test(L.get(curr))) {
                curr += 1;
            }
            return curr < L.size();
        }
        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T res = L.get(curr);
            curr += 1;
            return res;
        }
    }
}
