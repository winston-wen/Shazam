package shazam.db;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBPool {
    private static final String configFile = "shazam/db/dbcp.properties";
    private static DataSource dataSource;

    static {
        Properties dbProperties = new Properties();
        try {
            dbProperties.load(DBPool.class.getClassLoader().getResourceAsStream(configFile));
            dataSource = BasicDataSourceFactory.createDataSource(dbProperties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DBPool() {
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
