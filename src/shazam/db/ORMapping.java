package shazam.db;

import shazam.hash.ShazamHash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wen Ke on 2016/10/21.
 */
public class ORMapping {
    public static void insertHash(ShazamHash hash) {
        // TODO: insert a hash into DB
    }

    public static void insertHash(ShazamHash hash, String name) {
        hash.name = name;
        insertHash(hash);
    }

    public static List<ShazamHash> selectHash(int f1, int f2, int dt) {
        ArrayList<ShazamHash> hashes = new ArrayList<>();
        // TODO: select all hashes who has f1, f2 and dt
        // and store them into the array list.
        return hashes;
    }
}
