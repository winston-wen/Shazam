package shazam.db;

import shazam.hash.ShazamHash;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wen Ke on 2016/10/21.
 */
public class ORMapping {

    /**
     * Insert a hash into the database via a reused connection.
     * @param hash - The hash to insert.
     * @param conn - The connection object to be reused.
     */
    public static void insertHash(ShazamHash hash, Connection conn) {
        try (Statement stmt = conn.createStatement()){
            /**
             * Use `insert into ... returning xxx`
             * to avoid selection after insertion.
             */
            String sql1 = String.format("insert into hash (f1, f2, dt) values ('%d', '%d', '%d') returning hash_id;",
                    hash.f1, hash.f2, hash.dt);
            ResultSet rs = stmt.executeQuery(sql1);
            if (rs.next()) {
                int hash_id = rs.getInt("hash_id");
                String sql2 = String.format("insert into song_hash values ('%d', '%d', '%d');", hash.song_id, hash_id, hash.offset);
                stmt.execute(sql2);
            }
        } catch (SQLException e) {
            /**
             * According to
             * https://www.postgresql.org/docs/9.1/static/errcodes-appendix.html
             * The error code of violation of a unique constraint is "23505".
             * Such error should be ignored.
             */
            if (!e.getSQLState().equals("23505"))
                e.printStackTrace();
            else {
                try (Statement stmt = conn.createStatement()) {
                    String sql = String.format("select hash_id from hash where f1='%d' and f2='%d' and dt='%d'", hash.f1, hash.f2, hash.dt);
                    ResultSet rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        int hash_id = rs.getInt("hash_id");
                        String sql2 = String.format("insert into song_hash values ('%d', '%d', '%d');", hash.song_id, hash_id, hash.offset);
                        stmt.execute(sql2);
                    }
                } catch (SQLException e1) {
                    if (!e.getSQLState().equals("23505"))
                        e1.printStackTrace();
                }
            }
        }
    }

    /**
     * insert a song into the database.
     * @param song The un-encoded audio file name.
     * @return The song_id of the song just inserted.
     */
    public static int insertSong(String song, Connection conn) {
        // encode the song name to prevent SQL injection by ', " and `
        String encoded_name = null;
        try {
            encoded_name = URLEncoder.encode(song, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            Statement stmt = conn.createStatement();
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
    public static String getSongName(int id, Connection conn) {
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
     * Get the hash id according to the f1, f2 and dt fields of hash
     * @param hash
     * @param conn
     * @return
     */
    public static int getHashId(ShazamHash hash, Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = String.format("select hash_id from hash where f1='%d' and f2='%d' and dt='%d';",
                    hash.f1, hash.f2, hash.dt);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("hash_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     *
     * @param targetHash - The hash of target audio
     * @param hash_id - The hash_id fetched by getHashId
     * @param conn - Reuse the connection object
     * @return - A list of matching hashes
     */
    public static List<ShazamHash> selectHash(ShazamHash targetHash, int hash_id, Connection conn) {
        ArrayList<ShazamHash> hashes = new ArrayList<>();
        String sql = null;
        try (Statement stmt = conn.createStatement()) {
            sql = String.format("select \"offset\", song_id from song_hash where hash_id='%d';", hash_id);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ShazamHash hash = new ShazamHash();
                hash.f1 = targetHash.f1;
                hash.f2 = targetHash.f2;
                hash.dt = targetHash.dt;
                hash.offset = rs.getInt("offset");
                hash.song_id = rs.getInt("song_id");
                hashes.add(hash);
            }
        } catch (SQLException e) {
            System.err.println(sql);
            e.printStackTrace();
        }
        return hashes;
    }
}
