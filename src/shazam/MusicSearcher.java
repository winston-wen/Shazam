package shazam;

import java.io.File;

/**
 * Created by Administrator on 2016/10/20.
 *
 * TODO: Input a folder of noised music fragments.
 * TODO: Compute their fingerprints, match against fingerprints in DB, and find the best match.
 */
public class MusicSearcher {
    public static void main(String[] args) {
        if (args.length!=2) {
            throw new RuntimeException("usage: java MusicSearcher <input folder>");
        }
        File f = new File(args[1]);
        if (!f.isDirectory()) {
            throw new RuntimeException(String.format("%s is not a directory", args[1]));
        }
        File[] songs = f.listFiles();
        for (File song : songs) {
            // only process .wav files
            if (song.getName().toLowerCase().endsWith(".wav")) {

            }
        }
    }
}
