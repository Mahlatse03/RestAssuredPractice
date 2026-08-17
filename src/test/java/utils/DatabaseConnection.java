package utils;

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

    // Insert a new user into the RestAssuredUsers table. Returns the generated id, or -1 if none.
    public static void insertUser(String email, String password) throws SQLException {

        try (Connection connection = getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement
                    ("INSERT INTO RestAssuredUsers (email, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                // Set the parameters for the prepared statement
                ps.setString(1, email);
                ps.setString(2, password);
                ps.executeUpdate();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("Generated record key: " + generatedKeys.getInt(1));
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error inserting user: " + e.getMessage());
            }
        }
    }

    public static void getLoginsFromDB(String userEmail) throws SQLException {
        // Outer try-with-resources: auto-closes Connection
        try (Connection connection = getConnection()) {
            // Inner try-with-resources: auto-closes PreparedStatement
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM RestAssuredUsers WHERE email = ?")) {
                ps.setString(1, userEmail);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        getEmailAddress = resultSet.getString("email");
                        getPassword = resultSet.getString("password");
                        System.out.println("Email from DB: " + getEmailAddress );
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error executing query: " + e.getMessage());
            }
        }

    }

}
