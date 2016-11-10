package shazam.search;

import shazam.db.DBPool;
import shazam.db.ORMapping;

import java.sql.Connection;

/**
 * Created by Wen Ke on 2016/10/22.
 */
public class SongScore {
    public int id;
    public int score;
    public static Connection conn = DBPool.getConnection();

    @Override
    public String toString() {
        return String.format("score=%d for [%d](%s)", score, id, ORMapping.getSongName(id));
    }
}
