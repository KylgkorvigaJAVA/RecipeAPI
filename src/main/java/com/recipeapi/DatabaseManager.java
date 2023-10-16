package com.recipeapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String JDBC_URL = "jdbc:h2:~/recipedb";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static Connection connection;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager::closeConnection));
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("DB connected!");
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("DB connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
