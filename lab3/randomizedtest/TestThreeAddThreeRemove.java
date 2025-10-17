package randomizedtest;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestThreeAddThreeRemove {
    @Test
    public void testSimply() {
        AListNoResizing<Integer> a = new AListNoResizing<>();
        BuggyAList<Integer> b = new BuggyAList<>();
        a.addLast(1);
        a.addLast(2);
        a.addLast(3);
        b.addLast(1);
        b.addLast(2);
        b.addLast(3);
        assertEquals(a.size(), b.size());
        int t1 = a.removeLast();
        int t2 = b.removeLast();
        assertTrue(t1 == t2);
        t1 = a.removeLast();
        t2 = b.removeLast();
        assertTrue(t1 == t2);
        t1 = a.removeLast();
        t2 = b.removeLast();
        assertTrue(t1 == t2);
    }
}
