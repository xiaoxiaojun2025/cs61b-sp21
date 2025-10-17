package deque;

import net.sf.saxon.om.Item;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    /* test the correctness of size and isEmpty*/
    public void testIsEmptySize() {
        ArrayDeque<Integer> a1 = new ArrayDeque<>();
        assertEquals(0, a1.size());
        assertTrue(a1.isEmpty());
        a1.addFirst(2);
        assertEquals(1, a1.size());
        a1.addLast(3);
        assertEquals(2, a1.size());
    }
    @Test
    /* test the correctness of get and printDeque */
    public void testGetPrintDeque() {
        ArrayDeque<Integer> a1 = new ArrayDeque<>();
        a1.addFirst(3);
        a1.addLast(4);
        a1.addLast(5);
        a1.addLast(6);
        a1.addLast(7);
        Integer a = a1.get(0);
        Integer b = a1.get(2);
        Integer c = a1.get(4);
        Integer d = a1.get(-1);
        Integer e = a1.get(5);
        assertEquals(3, a, 0.0);
        assertEquals(5, b, 0.0);
        assertEquals(7, c, 0.0);
        assertNull(d);
        assertNull(e);
        a1.printDeque();
    }
    @Test
    /* add three elements and remove them
    to test removeLast and removeFirst
     */
    public void addThreeRemoveThree() {
        ArrayDeque<Integer> a1 = new ArrayDeque<>();
        a1.addFirst(3);
        a1.addLast(7);
        a1.addFirst(19);
        assertEquals(3, a1.size());
        a1.printDeque();
        Integer a = a1.removeFirst();
        assertEquals(2, a1.size());
        Integer b = a1.removeLast();
        assertEquals(1, a1.size());
        Integer c = a1.removeLast();
        assertEquals(0, a1.size());
        Integer d = a1.removeFirst();
        assertEquals(0, a1.size());
        Integer e = a1.removeLast();
        assertEquals(0, a1.size());
        assertEquals(19, a, 0.0);
        assertEquals(7, b, 0.0);
        assertEquals(3, c, 0.0);
        assertNull(d);
        assertNull(e);
    }
    @Test
    /* Add many elements and test the function of resize. */
    public void testManyElements() {
        ArrayDeque<Integer> a1 = new ArrayDeque<>();
        for (int i = 0 ; i < 10000; i += 1) {
            a1.addLast(i + 1);
        }
        System.out.println(a1.capacity());
        assertEquals(10000, a1.size());
        Integer a = a1.get(2999);
        assertEquals(3000, a, 0.0);
        for (int i = 0; i < 8000; i += 1) {
            a1.removeLast();
        }
        System.out.println(a1.capacity());
        Integer b = a1.get(2999);
        assertNull(b);
    }
    @Test
    /* Test different types of element. */
    public void testDifferentElements() {
        ArrayDeque<Integer> a1 = new ArrayDeque<>();
        ArrayDeque<Double> a2 = new ArrayDeque<>();
        ArrayDeque<String> a3 = new ArrayDeque<>();
        a1.addLast(89);
        a1.addFirst(66);
        a2.addFirst(3.1415925);
        a2.addLast(2.71);
        a3.addLast("hello world");
        a3.addLast("good");
        Integer a = a1.get(1);
        Double b = a2.get(0);
        String c = a3.get(1);
        String d = a3.get(2);
        assertNull(d);
        assertEquals(89, a, 0.0);
        assertEquals(3.1415925, b, 0.0);
        assertEquals("good", c);

    }
}
