package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    /** reuse arraydeque. */
    private Comparator<T> cmp;
    public MaxArrayDeque(Comparator<T> c) {
        super();
        cmp = c;
    }
    /** Return the max of Deque by cmp. */
    public T max() {
        return max(cmp);
    }
    /** Return the max of Deque by c. */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T res = get(0);
        for (int i = 1; i < size(); i += 1) {
            if (c.compare(get(i), res) > 0) {
                res = get(i);
            }
        }
        return res;
    }

}
