package tester;
import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.introcs.StdRandom;


public class TestArrayDequeEC {
    /* randomly test some example of StudentArrayDeque. */
    @Test
    public void testRandomly() {
        StudentArrayDeque<Integer> stu = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ans = new ArrayDequeSolution<>();
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < 1000; i += 1) {
            double randomNumber = StdRandom.uniform();
            if (randomNumber < 0.25) {
                stu.addFirst(i);
                ans.addFirst(i);
                message.append("addFirst(").append(i).append(")\n");
            } else if (randomNumber < 0.50) {
                stu.addLast(i);
                ans.addLast(i);
                message.append("addLast(").append(i).append(")\n");
            } else if (randomNumber < 0.75) {
                if (ans.isEmpty() || stu.isEmpty()) {
                    continue;
                }
                Integer a = stu.removeFirst();
                Integer b = ans.removeFirst();
                message.append("removeFirst()\n");
                assertEquals(message.toString(), b, a);
            } else {
                if (ans.isEmpty() || stu.isEmpty()) {
                    continue;
                }
                Integer a = stu.removeLast();
                Integer b = ans.removeLast();
                message.append("removeLast()\n");
                assertEquals(message.toString(), b, a);
            }
        }
    }
}
