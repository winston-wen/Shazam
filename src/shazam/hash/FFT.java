package shazam.hash;

/**
 * Created by Wen Ke on 2016/10/20.
 */
public class FFT {

    public static final int WINDOW_SIZE = 4096;

    /**
     * Perform FFT on a 4096-sample slice.
     * For a 44100 Hz audio, the slice is 4096/44100 sec, nearly 0.1s.
     * NOTE: The scale of the returned frequencies is 44100/4096 times of the array index.
     *
     * @param slice An audio slice (time-domain) with 4096 samples.
     * @return The frequency domain of <pre>slice</pre>, i.e. the magnitude of "each frequency".
     */
    static double[] fft(double[] slice) {
        if (slice.length!=WINDOW_SIZE)
            throw new RuntimeException("FFT::fft(double[] slice) - " +
                    "The window size is not equal to the required window size ("+WINDOW_SIZE+")");

        Complex[] x = new Complex[WINDOW_SIZE];

        /**
         * Convert the time-domain series as Complex series whose imaginary parts are zeros.
         */
        for (int i=0; i<WINDOW_SIZE; ++i) {
            x[i] = new Complex(slice[i], 0);
        }

        Complex[] res = fft(x);

        double[] ret = new double[WINDOW_SIZE];
        for (int i=0; i<WINDOW_SIZE; ++i) {
            /**
             * The magnitude of each frequency.
             */
            ret[i] = res[i].abs();
        }
        return ret;
    }

    private static Complex[] fft(Complex[] x) {
        int N = x.length;
        if (N<=1) return x;
        // fft of even terms
        Complex[] even = new Complex[N / 2];
        for (int k = 0; k < N / 2; k++) {
            even[k] = x[2 * k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd = even; // reuse the array
        for (int k = 0; k < N / 2; k++) {
            odd[k] = x[2 * k + 1];
        }
        Complex[] r = fft(odd);
        Complex[] y = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].plus(wk.times(r[k]));
            y[k + N / 2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
}
