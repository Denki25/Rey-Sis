package org.example.data;

import org.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class UserRepository {
    private static final String FIND_BY_USERNAME = """
            SELECT user_id, username, password_hash, role, is_active
            FROM users
            WHERE username = ?
            """;

    public User findByUsername(String username) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                return new User(
                        result.getInt("user_id"),
                        result.getString("username"),
                        result.getString("password_hash"),
                        result.getString("role"),
                        result.getBoolean("is_active"));
            }
        }
    }
}
