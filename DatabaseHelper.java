import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

public class DatabaseHelper {

    private static String URL;
    private static String USER;
    private static String PASS;

    // Load database credentials from config.properties
    static {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("config.properties");
            props.load(fis);

            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASS = props.getProperty("db.password");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Connection function
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Signup
    public static boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.out.println("User already exists!");
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Login
    public static boolean checkLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}