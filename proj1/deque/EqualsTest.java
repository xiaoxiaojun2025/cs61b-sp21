package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class EqualsTest {
    /* tset equals between LLD and AD. */
    @Test
    public void testEquals() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        LinkedListDeque<Integer> b = new LinkedListDeque<>();
        a.addFirst(3);
        a.addFirst(4);
        a.addFirst(5);
        a.addFirst(6);
        a.addFirst(7);
        b.addLast(7);
        b.addLast(6);
        b.addLast(5);
        b.addLast(4);
        b.addLast(3);
        assertTrue(a.equals(b));
    }
}
