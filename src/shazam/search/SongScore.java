package shazam.search;

import shazam.db.ORMapping;

/**
 * Created by Wen Ke on 2016/10/22.
 */
public class SongScore {
    public int id;
    public int score;

    @Override
    public String toString() {
        return String.format("score=%d for [%d](%s)", score, id, ORMapping.getSongName(id));
    }
}
