package shazam.search;

import shazam.db.ORMapping;
import shazam.hash.Hash;

import java.util.*;

/**
 * Created by Wen Ke on 2016/10/22.
 */
public class Grader {
    
    public static final int time_level = 5;   // each column of the histogram is approximately 0.5 sec
    
    public static ArrayList<SongScore> grade(final ArrayList<Hash> targetHashes) {
        return grade(gatherMatchingHashes(targetHashes));
    }
    
    private static Statistics gatherMatchingHashes(final ArrayList<Hash> targetHashes) {
        // sort target hashes by hash id
        // so that identical hash_id will be queried ONLY ONCE
        Collections.sort(targetHashes, new Comparator<Hash>() {
            @Override
            public int compare(Hash o1, Hash o2) {
                return o1.getHashID() - o2.getHashID();
            }
        });
        
        Statistics statistics = new Statistics();
        
        int last_hash_id = -1;
        int current_hash_id;
        List<Hash> matchingHashes = null;
        for (Hash targetHash : targetHashes) {
            current_hash_id = targetHash.getHashID();
            // if targetHash.getHashID is a new hash id.
            if (current_hash_id != last_hash_id) {
                matchingHashes = ORMapping.selectHash(targetHash);
            }
            // update last_hash_id
            last_hash_id = current_hash_id;
            for (Hash matchingHash : matchingHashes) {
                // if the song entry does not exist, create one
                if (!statistics.containsKey(matchingHash.song_id)) {
                    statistics.put(matchingHash.song_id, new ArrayList<>());
                }
                ArrayList<Integer> diffs = statistics.get(matchingHash.song_id);
                diffs.add(matchingHash.offset - targetHash.offset);
            }
        }
        matchingHashes = null;
        return statistics;
    }
    
    /**
     * return a grading result
     */
    private static ArrayList<SongScore> grade(Statistics stat) {
        ArrayList<SongScore> ret = new ArrayList<>();
        
        // iterate each possible song
        for (Iterator<Map.Entry<Integer, ArrayList<Integer>>> eachSong = stat.entrySet().iterator();
             eachSong.hasNext(); ) {
            Map.Entry<Integer, ArrayList<Integer>> entry = eachSong.next();
            Integer song_id = entry.getKey();
            ArrayList<Integer> song_time_diff = entry.getValue();
            
            // convert to a histogram
            Collections.sort(song_time_diff);
            for (int i = 0; i < song_time_diff.size(); ++i) {
                song_time_diff.set(i, song_time_diff.get(i) / time_level);
            }
            
            int max = 1, count = 1;
            
            // count how many times the "mode" appears.
            // mode: n. (statistics) The mode is the value that is repeated most often in a data set.
            for (int i = 0, j = 1; j < song_time_diff.size(); ++i, ++j) {
                // count the length of each sub-series with repeating elements.
                if (Objects.equals(song_time_diff.get(i), song_time_diff.get(j))) {
                    count++;
                }
                
                // if such sub-series terminates.
                else {
                    if (count > max) {
                        max = count;
                    }
                    count = 1;
                }
            }
            
            SongScore score = new SongScore();
            score.id = song_id;
            score.score = max;
            ret.add(score);
        }
        
        Collections.sort(ret, new Comparator<SongScore>() {
            @Override
            public int compare(SongScore o1, SongScore o2) {
                return o2.score - o1.score;
            }
        });
        
        return ret;
    }
}

