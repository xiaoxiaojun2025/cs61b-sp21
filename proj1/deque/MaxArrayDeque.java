package deque;

import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item> {
    /** reuse arraydeque. */
    private Comparator<Item> cmp;
    public MaxArrayDeque(Comparator<Item> c) {
        super();
        cmp = c;
    }
    /** Return the max of Deque by cmp. */
    public Item max() {
        return max(cmp);
    }
    /** Return the max of Deque by c. */
    public Item max(Comparator<Item> c) {
        if (isEmpty()) {
            return null;
        }
        Item res = get(0);
        for (int i = 1; i < size(); i += 1) {
            if (c.compare(get(i), res) > 0) {
                res = get(i);
            }
        }
        return res;
    }

}
