package flik;

import org.junit.Test;

import static org.junit.Assert.*;
public class FlikTest {
    /* randomly test. */
    @Test
    public void testRandomly() {
        for (int i = -1000; i < 1000; i += 1) {
            Integer a = i;
            Integer b = i;
            assertTrue("ohnoo", Flik.isSameNumber(a, b));
        }
    }
}
