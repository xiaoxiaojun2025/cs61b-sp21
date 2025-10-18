package deque;

import org.junit.Test;

import java.util.Comparator;
import static org.junit.Assert.*;


public class MaxArrayDequeTest {
    @Test
    /* Simply test MaxArrayDeque. */
    public void testMaxArrayDeque() {
        /* compare by the length of the string. */
        Comparator<String> s1 = new Comparator<>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        };
        /* compare the first element of the string. */
        Comparator<String> s2 = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.isEmpty() && o2.isEmpty()) {
                    return 0;
                } else if (o1.isEmpty()) {
                    return -1;
                } else if (o2.isEmpty()) {
                    return 1;
                } else {
                    return o1.charAt(0) - o2.charAt(0);
                }
            }
        };
        Comparator<Integer> i = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };
        Comparator<Double> d = new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o1.compareTo(o2);
            }
        };
        MaxArrayDeque<String> mad1 = new MaxArrayDeque<>(s1);
        MaxArrayDeque<String> mad2 = new MaxArrayDeque<>(s2);
        MaxArrayDeque<Integer> mad3 = new MaxArrayDeque<>(i);
        MaxArrayDeque<Double> mad4 = new MaxArrayDeque<>(d);
        mad1.addFirst("cbdsu.ka");
        mad1.addFirst("bhyafvgy");
        mad1.addFirst("ewaj");
        mad1.addFirst("153");
        mad1.addFirst("");
        assertEquals("bhyafvgy", mad1.max());
        assertEquals("ewaj", mad1.max(s2));
        mad2.addFirst("vgnfdvh");
        mad2.addFirst("8fv4d6");
        mad2.addFirst("tuurguagd");
        mad2.addFirst("hello world");
        mad2.addLast("");
        assertEquals("hello world", mad2.max(s1));
        assertEquals("vgnfdvh", mad2.max());
        mad3.addLast(5);
        mad3.addLast(89);
        mad3.addLast(56);
        mad3.addLast(99);
        mad3.addLast(77);
        mad3.addLast(-10000);
        assertEquals(99, mad3.max(), 0);
        mad4.addLast(0.5);
        mad4.addLast(486.2);
        mad4.addLast(89.0);
        mad4.addLast(9.6);
        mad4.addLast(7.6);
        mad4.addLast(0.5);
        assertEquals(486.2, mad4.max(), 0);
        MaxArrayDeque<Integer> mad5 = new MaxArrayDeque<>(i);
        assertNull(mad5.max());
    }
}
