package shazam.hash;

import shazam.pcm.PCM16MonoData;
import shazam.pcm.PCM16MonoParser;

import java.io.File;
import java.io.IOException;

/**
 * Created by Wen Ke on 2016/11/4.
 */
public class FFTTest {
    public static void main(String[] args) throws IOException {
        PCM16MonoData data = PCM16MonoParser.parse(new File("data/Volcanoes-piece.wav"));
        double[] frame_samples = new double[FFT.WINDOW_SIZE];
        for (int j = 0; j < FFT.WINDOW_SIZE; ++j) {
            frame_samples[j] = data.getSample(j);
        }

        /**
         * call FFT to convert to frequency domain.
         */
        double[] freq_dom = FFT.fft(frame_samples);

        System.out.print("[ ");
        for (double f : freq_dom) {
            System.out.printf("%.2f ", f);
        }
        System.out.println(" ];");
    }
}
