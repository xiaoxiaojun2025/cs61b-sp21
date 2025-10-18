package gh2;

import edu.princeton.cs.introcs.StdAudio;
import org.junit.Test;

public class TsetHarpString {
    @Test
    public void testPluckTheAString() {
        HarpString aString = new HarpString(GuitarHeroLite.CONCERT_A);
        aString.pluck();
        for (int i = 0; i < 50000; i += 1) {
            StdAudio.play(aString.sample());
            aString.tic();
        }
    }
}
