package org.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String URL = environment("REY_SIS_DB_URL", "jdbc:mysql://localhost:3306/rey_sis");
    private static final String USER = environment("REY_SIS_DB_USER", "root");
    private static final String PASSWORD = environment("REY_SIS_DB_PASSWORD", "denricmendoza@25");
    private static volatile boolean schemaReady;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (USER.isBlank()) {
            throw new SQLException("REY_SIS_DB_USER is not configured.");
        }
        initializeSchema();
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void initializeSchema() throws SQLException {
        if (schemaReady) {
            return;
        }
        synchronized (DatabaseConnection.class) {
            if (schemaReady) {
                return;
            }

            Connection connection;
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException connectionException) {
                try {
                    DatabaseSchema.ensureReady(URL, USER, PASSWORD);
                } catch (SQLException setupException) {
                    setupException.addSuppressed(connectionException);
                    throw setupException;
                }
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }

            try (Connection connectionToInitialize = connection) {
                DatabaseSchema.ensureReady(connectionToInitialize);
            }
            schemaReady = true;
        }
    }

    private static String environment(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
