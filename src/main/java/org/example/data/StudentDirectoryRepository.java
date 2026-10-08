package org.example.data;

import org.example.auth.PasswordHashing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.GeneralSecurityException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public final class StudentDirectoryRepository {
    public List<StudentSummary> findAll() throws SQLException {
        String sql = "SELECT s.student_id, CONCAT(s.first_name, ' ', s.last_name) AS student_name, "
                + "s.program, s.year_level, CASE WHEN EXISTS (SELECT 1 FROM enrollments e "
                + "WHERE e.student_id = s.student_id AND (e.status = 'ENROLLED' OR e.status IS NULL)) "
                + "THEN 'Enrolled' ELSE 'Unenrolled' END AS enrollment_status "
                + "FROM students s ORDER BY s.last_name, s.first_name";
        List<StudentSummary> students = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                students.add(new StudentSummary(result.getString("student_id"), result.getString("student_name"),
                        result.getString("program"), result.getString("year_level"), result.getString("enrollment_status")));
            }
        }
        return students;
    }

    public CreatedStudent createStudent(String firstName, String lastName, String program, String yearLevel,
                                        String section, String email) throws SQLException {
        String username;
        int studentYear = Year.now().getValue();
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String studentId = DatabaseSchema.nextStudentId(connection, studentYear);
                username = studentId;
                char[] initialPassword = "123".toCharArray();
                String passwordHash;
                try {
                    passwordHash = PasswordHashing.hash(initialPassword);
                } catch (GeneralSecurityException exception) {
                    throw new SQLException("Unable to prepare the student's initial password.", exception);
                } finally {
                    java.util.Arrays.fill(initialPassword, '\0');
                }

                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO users (username, password_hash, role, is_active) VALUES (?, ?, 'STUDENT', TRUE)")) {
                    statement.setString(1, username);
                    statement.setString(2, passwordHash);
                    statement.executeUpdate();
                }

                int userId;
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT user_id FROM users WHERE username = ?")) {
                    statement.setString(1, username);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            throw new SQLException("The new student login account was not created.");
                        }
                        userId = result.getInt("user_id");
                    }
                }

                DatabaseSchema.insertStudent(connection, studentId, userId, firstName, lastName, program,
                        yearLevel, section, email);
                connection.commit();
                return new CreatedStudent(studentId, username);
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public record CreatedStudent(String studentId, String username) {
    }

    public record StudentSummary(String id, String name, String program, String yearLevel, String enrollmentStatus) {
    }
}
