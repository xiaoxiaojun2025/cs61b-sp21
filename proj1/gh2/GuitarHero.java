package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    private static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {
        GuitarString[] s = new GuitarString[37];
        for (int i = 0; i < 37; i += 1) {
            double frequency = 440 * Math.pow(2, (i - 24) / 12);
            s[i] = new GuitarString(frequency);
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index == -1) {
                    continue;
                }
                s[index].pluck();
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for (int i = 0; i < 37; i += 1) {
                sample += s[i].sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < 37; i += 1) {
                s[i].tic();
            }
        }
    }
}
