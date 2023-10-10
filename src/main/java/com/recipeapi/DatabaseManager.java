package com.recipeapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String JDBC_URL = "jdbc:h2:~/recipedb";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                if (connection != null) {
                    connection.close();
                    System.out.println("DB closed!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("¤¤¤¤¤");
            }
        }));
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}
