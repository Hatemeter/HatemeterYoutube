package eu.fbk.dh.HatemeterYoutube.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConnectionManager {
    private static String username=null;
    private static String password=null;

    public static Connection getConnection() {
        Connection con=null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try {
                try {
                    InputStream input = JDBCConnectionManager.class.getClassLoader().getResourceAsStream("dbcredentials.properties");
                    Properties prop = new Properties();
                    prop.load(input);
                    username = prop.getProperty("mysqlUser");
                    password= prop.getProperty("mysqlPassword");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/HateMeter?autoreconnect=true&allowMultiQueries=true&connectTimeout=0&socketTimeout=0&useUnicode=yes&characterEncoding=UTF-8&serverTimezone=UTC", username, password);
            } catch (SQLException ex) {
                System.out.println("Failed to create the database connection.");
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver not found.");
        }
        return con;
    }
}
