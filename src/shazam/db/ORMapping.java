package shazam.db;

import shazam.hash.ShazamHash;

import java.io.File;
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

    public static void insertHash(ShazamHash hash, String name) {
        try (Connection conn = DBPool.getConnection()) {
            Statement stmt1 = conn.createStatement();
            String sql1 = String.format("select id from song where name='%s';", name);
            ResultSet rs1 = stmt1.executeQuery(sql1);
            int id = -1;
            if (rs1.next()) {
                id = rs1.getInt("id");
                Statement stmt2 = conn.createStatement();
                String sql2 = String.format("insert into hash values ('%d', '%d', '%d', '%d', '%d');",
                        hash.f1, hash.f2, hash.dt, hash.offset, id);
                stmt2.execute(sql2);
            } else { // The song does not exist in DB
                throw new RuntimeException("No such song in DB");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertSong(String song) {
        File f = new File(song);
        String name = f.getName().replaceAll("\\.wav", "");
        try (Connection conn = DBPool.getConnection(); Statement stmt = conn.createStatement()) {
            String sql = String.format("insert into song (name) values ('%s');", name);
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
        return ret;
    }

    public static List<ShazamHash> selectHash(short f1, short f2, short dt) {
        ArrayList<ShazamHash> hashes = new ArrayList<>();
        try (Connection conn = DBPool.getConnection(); Statement stmt = conn.createStatement()) {
            String sql = String.format("select * from hash h join song s on h.id = s.id where f1='%d' and f2='%d' and dt='%d' group by id;", f1, f2, dt);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ShazamHash hash = new ShazamHash();
                hash.f1 = rs.getShort("f1");
                hash.f2 = rs.getShort("f2");
                hash.dt = rs.getShort("dt");
                hash.offset = rs.getInt("offset");
                hash.id = rs.getInt("id");
                hashes.add(hash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashes;
    }
}
