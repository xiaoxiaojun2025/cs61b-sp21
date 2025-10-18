package gh2;

import deque.ArrayDeque;
import deque.Deque;
import edu.princeton.cs.algs4.StdAudio;

public class Drum {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = 1.0; // energy decay factor

    public int getSR() {
        return SR;
    }
    /* Buffer for storing sound data. */
    private Deque<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public Drum(double frequency) {
        buffer = new ArrayDeque<>();
        long capacity = 2 * Math.round(SR / frequency);
        for (int i = 0; i < capacity; i += 1) {
            buffer.addLast(0.0);
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        for (int i = 0; i < buffer.size(); i += 1) {
            buffer.removeLast();
            buffer.addFirst(Math.random() - 0.5);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double temp = buffer.removeFirst();
        buffer.addLast((temp + buffer.get(0)) / 2 * DECAY
                * (Math.round(Math.random()) * 2 - 1));
    }
    //

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.get(0);
    }

    public static void main(String[] args) {
        Drum drum = new Drum(100);
        drum.pluck();
        for (int i = 0; i < 50000; i += 1) {
            StdAudio.play(drum.sample());
            drum.tic();
        }
    }
}
