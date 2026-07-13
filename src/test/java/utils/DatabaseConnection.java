package utils;

import com.beust.jcommander.JCommander;

import java.sql.*;

public class DatabaseConnection {
    public static String getEmailAddress;
    public static String getPassword;

    public static void dbConnection(String userEmail) throws SQLException {
        // Get DB credentials from environment variables
        String dbURL = commons.Routes.DB_URL;
        String dbUsername = commons.Routes.DB_USERNAME;
        String dbPassword = commons.Routes.DB_PASSWORD;


        // Outer try-with-resources: auto-closes Connection
        try (Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword)) {
            // Inner try-with-resources: auto-closes Statement and ResultSet
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM RestAssuredLogins WHERE email = '" + userEmail + "'")) {

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

    // Insert a new user into the loginUser table. Returns the generated id, or -1 if none.
    public static int insertUser(String email, String password) throws SQLException {
        String dbURL = commons.Routes.DB_URL;
        String dbUsername = commons.Routes.DB_USERNAME;
        String dbPassword = commons.Routes.DB_PASSWORD;

        String sql = "INSERT INTO loginUser (email, password) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, password);
            int affected = ps.executeUpdate();

            if (affected == 0) {
                throw new SQLException("Inserting user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    return -1;
                }
            }
        }
    }

    // Select a user by id and return a lightweight User object, or null if not found.
    public static User selectUserById(int id) throws SQLException {
        String dbURL = commons.Routes.DB_URL;
        String dbUsername = commons.Routes.DB_USERNAME;
        String dbPassword = commons.Routes.DB_PASSWORD;

        String sql = "SELECT id, email, password FROM loginUser WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
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
