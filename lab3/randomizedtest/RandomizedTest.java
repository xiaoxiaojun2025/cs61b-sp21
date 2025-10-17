package randomizedtest;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;
public class RandomizedTest {
    @Test
    public void testSimply() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L1 = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L1.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size1 = L1.size();
                assertEquals(size, size1);
            } else if (operationNumber == 2 && L.size() > 0 && L1.size() > 0) {
                int last = L.getLast();
                int last1 = L1.getLast();
                assertEquals(last, last1);
            } else if (operationNumber == 3 && L.size() > 0 && L1.size() > 0) {
                int removeLast = L.removeLast();
                int removeLast1 = L1.removeLast();
                assertEquals(removeLast, removeLast1);
            }
        }
    }
}
