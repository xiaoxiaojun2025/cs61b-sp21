package bstmap;

import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class MyTest {
    /*Test iterator. */
    @Test
    public void iteratorTest() {
        BSTMap<Integer, String> a = new BSTMap<>();
        a.put(3, "woshihaoren");
        a.put(9, "woshihuairen");
        a.put(-1, "woshidahuaidan");
        a.put(4, "woshihaoxuesheng");
        Iterator<Integer> it = a.iterator();
        assertEquals(Integer.valueOf(-1), it.next());
        assertEquals(Integer.valueOf(3), it.next());
        assertEquals(Integer.valueOf(4), it.next());
        assertEquals(Integer.valueOf(9), it.next());
    }
    /* Test printInOrder. */
    @Test
    public void printTest() {
        BSTMap<String, Integer> a = new BSTMap<>();
        BSTMap<String, Integer> b = new BSTMap<>();
        a.put("apple", 1);
        a.put("egg", 2);
        a.put("zip", 5);
        a.put("mia", 4);
        a.put("hero", 3);
        a.printInOrder();
        b.printInOrder();
    }
    /* Test REMOVE. */
    @Test
    public void removeTest() {
        /* Remove root. */
        BSTMap<Integer, String> a = new BSTMap<>();
        a.put(3, "good");
        String s = a.remove(3);
        assertEquals(0, a.size());
        assertEquals("good", s);
        /* Remove leaf. */
        BSTMap<Integer, String> b = new BSTMap<>();
        b.put(3, "g");
        b.put(4, "go");
        b.put(5, "goo");
        b.put(6, "good");
        s = b.remove(6);
        assertEquals("good", s);
        /* Remove a node with only one child. */
        b.put(6, "good");
        s = b.remove(4);
        assertEquals("go", s);
        assertEquals(3, b.size());
        /* Remove a node with two children. */
        b.put(4, "go");
        s = b.remove(5);
        assertEquals("goo", s);
        b.printInOrder();
        assertNull(b.remove(10));
    }
    /* Randomly remove nodes. */
    @Test
    public void randomlyRemoveTest() {
        BSTMap<String, Integer> a = new BSTMap<>();
        Set<String> keys = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i += 1) {
            Integer v = random.nextInt(10000);
            String s = "hi" + v;
            keys.add(s);
            a.put(s, v);
        }
        for (int i = 0; i < 10000; i += 1) {
            int v = random.nextInt(10000);
            String s = "hi" + v;
            Integer res = a.remove(s);
            if (keys.contains(s)) {
                keys.remove(s);
                assertEquals(Integer.valueOf(v), res);
                assertEquals(keys.size(), a.size());
            } else {
                assertNull(res);
                assertEquals(keys.size(), a.size());
            }
        }
    }
}
