package shazam;

import shazam.db.ORMapping;
import shazam.hash.ConstellationMap;
import shazam.hash.FFT;
import shazam.hash.ShazamHash;
import shazam.pcm.PCM16MonoData;
import shazam.pcm.PCM16MonoParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/10/20.
 *
 * TODO: Input a folder which contains the musics for the database.
 * TODO: Compute the fingerprints of each music and insert into the database.
 */
public class MusicArchiver {

    public static void main(String[] args) throws IOException {
        System.out.println("Enter the directory path at the next line");
        Scanner in = new Scanner(System.in);
        File f = new File(in.nextLine());
        in.close();
        if (!f.isDirectory()) {
            throw new RuntimeException(String.format("%s is not a directory", args[1]));
        }
        File[] songs = f.listFiles();
        for (File song : songs) {
            /**
             * only process .wav files
             */
            if (song.getName().toLowerCase().endsWith(".wav")) {
                System.out.println("Processing "+song.getName()+" ...");
                /**
                 * add a song
                 */
                ORMapping.insertSong(song.getName());

                /**
                 * extract PCM data
                 */
                PCM16MonoData data = PCM16MonoParser.parse(song);
                ConstellationMap map = new ConstellationMap();

                for (int i=0; i<data.getSampleNum();) {
                    /**
                     * collect 2 frames of samples, which should be FFT.WINDOW_SIZE;
                     */
                    double[] frame_samples = new double[FFT.WINDOW_SIZE];
                    for (;i<data.getSampleNum() && (i+1)%FFT.WINDOW_SIZE!=0; ++i) {
                        frame_samples[i%FFT.WINDOW_SIZE] = data.getSample(i);
                    }

                    /**
                     * call FFT to convert to frequency domain.
                     */
                    double[] freq_dom = FFT.fft(frame_samples);

                    /**
                     * append the frequency domain info to the constellation map
                     */
                    map.append(freq_dom);
                }

                /**
                 * Generate fingerprints;
                 */
                ArrayList<ShazamHash> hashes = map.shazamHash();

                /**
                 * Insert fingerprints;
                 */
                for (ShazamHash hash: hashes) {
                    ORMapping.insertHash(hash, song.getName());
                }

                System.out.println("Finish processing "+song.getName()+" !");

            }
        }
    }
}
