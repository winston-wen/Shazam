package shazam.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Wen Ke on 2016/10/22.
 * Test the connectivity to the database.
 */
public class DBTest {
    public static void main(String[] args) {
        try (Connection conn = DBPool.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("insert into song (name) values ('xxxxx') returning song_id;");
            if (rs.next()) {
                System.out.println(rs.getInt("song_id"));
            }
        } catch (SQLException e) {
            System.err.println(e.getErrorCode());
            System.err.println(e.getSQLState());
            e.printStackTrace();
        }
    }
}
