package utils;

import javax.swing.plaf.nimbus.State;
import java.sql.*;

public class DatabaseConnection {
    public static String getEmailAddress;
    public static String getPassword;

    // Reusable method to establish database connection
    private static Connection getConnection() throws SQLException {
        String dbURL = commons.Routes.DB_URL;
        String dbUsername = commons.Routes.DB_USERNAME;
        String dbPassword = commons.Routes.DB_PASSWORD;
        return DriverManager.getConnection(dbURL, dbUsername, dbPassword);
    }

    public static void getLoginsFromDB(String userEmail) throws SQLException {


        // Outer try-with-resources: auto-closes Connection
        try (Connection connection = getConnection()) {
            // Inner try-with-resources: auto-closes Statement and ResultSet
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM RestAssuredUsers WHERE email = '" + userEmail + "'")) {

                while (resultSet.next()) {
                    getEmailAddress = resultSet.getString("email");
                    getPassword = resultSet.getString("password");
                    System.out.println("Email: " + getEmailAddress + ", Password: " + getPassword);
                }
            } catch (SQLException e) {
                System.out.println("Error executing query: " + e.getMessage());
            }// Connection auto-closes here, even if exception occurs, preventing connection leaks
            //No manual close() calls needed—the JVM handles cleanup automatically
        }

    }

    // Insert a new user into the RestAssuredUsers table. Returns the generated id, or -1 if none.
    public static int insertUser(String email, String password) throws SQLException {
        String sql = "INSERT INTO RestAssuredUsers (email, password) VALUES (?, ?)";

        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, email);
                ps.setString(2, password);
                ps.executeUpdate();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        return -1;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error inserting user: " + e.getMessage());
                return -1;
            }
        }
    }

    // Select a user by id and return a lightweight User object, or null if not found.
    public static User selectUserById(int id) throws SQLException {
        String sql = "SELECT id, email, password FROM RestAssuredUsers WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int foundId = rs.getInt("id");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    return new User(foundId, email, password);
                } else {
                    return null;
                }
            }
        }
    }

    // Simple container for user data returned from selectUserById
    public static class User {
        public final int id;
        public final String email;
        public final String password;

        public User(int id, String email, String password) {
            this.id = id;
            this.email = email;
            this.password = password;
        }

        @Override
        public String toString() {
            return "User{id=" + id + ", email='" + email + "', password='" + password + "'}";
        }
    }
}
