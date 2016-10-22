package shazam.search;

import shazam.db.ORMapping;
import shazam.hash.ShazamHash;

import java.util.*;

/**
 * Created by Wen Ke on 2016/10/22.
 */
public class Grader {

    public static final int time_level = 5;   // each column of the histogram is approximately 0.5 sec

    public static ArrayList<SongScore> grade(ArrayList<ShazamHash> targetHashes) {
        return grade(gatherMatchingHashes(targetHashes));
    }

    private static Statistics gatherMatchingHashes(ArrayList<ShazamHash> targetHashes) {
        Statistics statistics = new Statistics();
        for (ShazamHash targetHash : targetHashes) {
            /**
             * For each hash from the target hashes, find all its occurrences in the database.
             */
            List<ShazamHash> matchingHashes = ORMapping.selectHash(targetHash.f1, targetHash.f2, targetHash.dt);
            for (ShazamHash matchingHash : matchingHashes) {
                if (!statistics.containsKey(matchingHash.id)) {
                    statistics.put(matchingHash.id, new ArrayList<>());
                }
                ArrayList<Integer> diffs = statistics.get(matchingHash.id);
                diffs.add(matchingHash.offset - targetHash.offset);
            }
        }
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
            Collections.sort(song_time_diff);

            for (int i = 0; i < song_time_diff.size(); ++i) {
                /**
                 * Convert to a histogram
                 */
                song_time_diff.set(i, song_time_diff.get(i) / time_level);
            }

            int max = 1, count = 1;

            for (int i = 0, j = 1; j < song_time_diff.size(); ++i, ++j) {
                /**
                 * Count the length af an identical sub-series.
                 */
                if (Objects.equals(song_time_diff.get(i), song_time_diff.get(j))) {
                    count++;
                }
                // if the sub-series terminates.
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
                int s1 = o1.score;
                int s2 = o2.score;
                if (s1 == s2) return 0;
                else if (s1 > s2) return -1;
                else return 1;
            }
        });

        return ret;
    }
}

