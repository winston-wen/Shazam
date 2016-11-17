package shazam;

import shazam.db.ORMapping;
import shazam.hash.CombineHash;
import shazam.hash.FFT;
import shazam.hash.Hash;
import shazam.pcm.PCM16MonoData;
import shazam.pcm.PCM16MonoParser;

import java.io.File;
import java.io.IOException;
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

    public static void main(String[] args) throws IOException, SQLException {
        System.out.println("Enter the directory path at the next line");
        Scanner in = new Scanner(System.in);
        File f = new File(in.nextLine().replaceAll("\"", ""));
        in.close();
        if (!f.isDirectory()) {
            throw new RuntimeException(String.format("%s is not a directory", args[1]));
        }
        long start = System.currentTimeMillis();
        File[] songs = f.listFiles();
        for (File song : songs) {
            /**
             * only process .wav files
             */
            if (song.getName().toLowerCase().endsWith(".wav")) {
                long song_start = System.currentTimeMillis();
                System.out.println("Generating fingerprint for " + song.getName() + " ...");
                
                // register a song and get its id.
                int id = ORMapping.insertSong(song.getName());
                System.out.printf("Get id %d\n", id);
                
                // generate hashes.
                ArrayList<Hash> hashes = CombineHash.generateFingerprint(song, id);
                ORMapping.insertHash(hashes);
                
                // clear mem space.
                hashes = null;
                System.gc();
                
                long song_end = System.currentTimeMillis();
                System.out.printf("Finish generating fingerprints, time elapsed : %.2f!\n==============\n", (song_end-song_start)/1000.0);
            }
        }
        System.out.print("Building index ...");
        ORMapping.buildIndex();
        long end = System.currentTimeMillis();
        System.out.printf("Finish building index ! All tasks finished in %.2f seconds!\n", (end-start)/1000.0);

    }
}
