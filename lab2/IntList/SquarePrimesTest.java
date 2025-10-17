package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    @Test
    public  void testSquarePrimeForMore() {
        IntList a = IntList.of(2, 4, 9, 7, 16, 13);
        IntList b = IntList.of(2, 15, 17, 8, 9, 67);
        boolean result_a = IntListExercises.squarePrimes(a);
        boolean result_b = IntListExercises.squarePrimes(b);
        assertEquals("4 -> 4 -> 9 -> 49 -> 16 -> 169", a.toString());
        assertTrue(result_a);
        assertEquals("4 -> 15 -> 289 -> 8 -> 9 -> 4489", b.toString());
        assertTrue(result_b);
    }
}
