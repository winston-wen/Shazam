package shazam.db;

import shazam.hash.Hash;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wen Ke on 2016/10/21.
 */
public class ORMapping {

    private static Connection conn = DBPool.getConnection();
    /**
     * Insert a hash into the database via a reused connection.
     * @param hashes - The hash to insert.
     *
     */
    public static void insertHash(ArrayList<Hash> hashes) {
        try (Statement stmt = conn.createStatement()) {
            for (Hash hash: hashes) {
                String sql = String.format("insert into song_hash (hash_id, song_id,\"offset\") values ('%d', '%d', '%d');", hash.getHashID(), hash.song_id, hash.offset);
                stmt.addBatch(sql);
                sql = null;
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            if (!e.getSQLState().equals("23505"))
                e.printStackTrace();
        }
    }

    public static void buildIndex() {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("create index \"hash_id_idx\" on \"song_hash\" using btree(\"hash_id\");" +
                    "alter table \"song_hash\" cluster on \"hash_id_idx\";");
            stmt.close();
            stmt = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * insert a song into the database.
     * @param song The un-encoded audio file name.
     * @return The song_id of the song just inserted.
     */
    public static int insertSong(String song) {
        // encode the song name to prevent SQL injection by ', " and `
        String encoded_name = null;
        try {
            encoded_name = URLEncoder.encode(song, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try (Statement stmt = conn.createStatement()) {
            String sql = String.format("insert into song (name) values ('%s') returning song_id;", encoded_name);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("song_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // just cheating the compiler
        return -1;
    }

    /**
     * Get the song name by song_id.
     * @param id
     * @return The url-decoded song string.
     */
    public static String getSongName(int id) {
        String ret = "";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(String.format("select name from song where song_id='%d';", id));
            if (rs.next()) {
                ret = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ret = URLDecoder.decode(ret, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return ret;
    }

    /**
     *
     * @param targetHash - The hash of target audio
     * @return - A list of matching hashes
     */
    public static List<Hash> selectHash(Hash targetHash) {
        ArrayList<Hash> hashes = new ArrayList<>();
        String sql = "select song_id, \"offset\" from song_hash where hash_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, targetHash.getHashID());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Hash hash = new Hash();
                hash.setHashID(targetHash.getHashID());
                hash.offset = rs.getInt("offset");
                hash.song_id = rs.getInt("song_id");
                hashes.add(hash);
                hash = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashes;
    }
}
