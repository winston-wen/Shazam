package shazam;

import shazam.db.DBPool;
import shazam.db.ORMapping;
import shazam.hash.CombineHash;
import shazam.hash.FFT;
import shazam.hash.ShazamHash;
import shazam.pcm.PCM16MonoData;
import shazam.pcm.PCM16MonoParser;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/10/20.
 * <p>
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
                System.out.println("Processing " + song.getName() + " ...");
                /**
                 * add a song
                 */
                int id = ORMapping.insertSong(song.getName());

                /**
                 * extract PCM data
                 */
                PCM16MonoData data = PCM16MonoParser.parse(song);
                CombineHash map = new CombineHash(id);

                for (int i = 0; i < data.getSampleNum(); ) {
                    /**
                     * collect 2 frames of samples, which should be FFT.WINDOW_SIZE;
                     */
                    double[] frame_samples = new double[FFT.WINDOW_SIZE];
                    for (int j=0; i < data.getSampleNum() && j < FFT.WINDOW_SIZE; ++i, ++j) {
                        frame_samples[j] = data.getSample(i);
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
                try (Connection conn = DBPool.getConnection()) {
                    for (ShazamHash hash : hashes) {
                        ORMapping.insertHash(hash, conn);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                System.out.println("Finish processing " + song.getName() + " !");

            }
        }
    }
}
