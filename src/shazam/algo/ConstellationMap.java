package shazam.algo;

/**
 * Created by Wen Ke on 2016/10/20.
 * Generating a constellation map for each audio.
 */
public class ConstellationMap {

    private static final double scaling = FFT.WINDOW_SIZE/44100;

    private static int[][] freqRanges = new int[4][2];

    static {
        // bass and bass drum
        freqRanges[0][0] = (int) Math.ceil(20*scaling);
        freqRanges[0][1] = (int) Math.floor(300*scaling);

        // human base, guitar,
        freqRanges[1][0] = (int) Math.ceil(300*scaling);
        freqRanges[1][1] = (int) Math.floor(1200*scaling);

        // violin, piano
        freqRanges[2][0] = (int) Math.ceil(1200*scaling);
        freqRanges[2][1] = (int) Math.floor(2400*scaling);

        // piano, heavy-metal
        freqRanges[3][0] = (int) Math.ceil(2400*scaling);
        freqRanges[3][1] = (int) Math.floor(7200*scaling);
    }

    
}
