package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> NS = new AList<>();
        NS.addLast(8000);
        NS.addLast(16000);
        NS.addLast(32000);
        NS.addLast(128000);
        NS.addLast(512000);
        NS.addLast(1024000);
        AList<Double> times = new AList<>();
        for (int i = 0; i < NS.size(); i += 1) {
            Stopwatch sw = new Stopwatch();
            AList<Integer> temp = new AList<>();
            for (int j = 0; j <= NS.get(i); j += 1) {
                temp.addLast(1);
            }
            double tempTimes = sw.elapsedTime();
            times.addLast(tempTimes);
        }
        printTimingTable(NS, times, NS);
    }
}
