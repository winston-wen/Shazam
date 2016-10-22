package shazam.db;

import shazam.hash.ShazamHash;

import java.io.File;
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
     * @param hash The hash to insert.
     * @param conn The connection object to be reused.
     */
    public static void insertHash(ShazamHash hash, Connection conn) {
        try (Statement stmt = conn.createStatement()){
            String sql2 = String.format("insert into hash values ('%d', '%d', '%d', '%d', '%d');",
                    hash.f1, hash.f2, hash.dt, hash.offset, hash.id);
            stmt.execute(sql2);

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * insert a song into the database.
     * @param song The un-encoded audio file name.
     * @return The id of the song just inserted.
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
        try (Connection conn = DBPool.getConnection()) {
            Statement stmt = conn.createStatement();
            String sql = String.format("insert into song (name) values ('%s');", encoded_name);
            stmt.execute(sql);
            stmt.close();
            Statement st2 = conn.createStatement();
            sql = String.format("select id from song where name='%s'", encoded_name);
            ResultSet res = st2.executeQuery(sql);
            if (res.next()) {
                return res.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // just cheating the compiler
        return -1;
    }

    /**
     * Get the song name by id.
     * @param id
     * @return The url-decoded song string.
     */
    public static String getSongName(int id) {
        String ret = "";
        try (Connection conn = DBPool.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(String.format("select name from song where id='%d';", id));
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

    public static List<ShazamHash> selectHash(short f1, short f2, short dt) {
        ArrayList<ShazamHash> hashes = new ArrayList<>();
        String sql = null;
        try (Connection conn = DBPool.getConnection(); Statement stmt = conn.createStatement()) {
            sql = String.format("select f1, f2, dt, \"offset\", s.id as song_id from hash h inner join song s on h.id = s.id where f1='%d' and f2='%d' and dt='%d' order by s.id;", f1, f2, dt);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ShazamHash hash = new ShazamHash();
                hash.f1 = rs.getShort("f1");
                hash.f2 = rs.getShort("f2");
                hash.dt = rs.getShort("dt");
                hash.offset = rs.getInt("offset");
                hash.id = rs.getInt("song_id");
                hashes.add(hash);
            }
        } catch (SQLException e) {
            System.err.println(sql);
            e.printStackTrace();
        }
        return hashes;
    }
}
