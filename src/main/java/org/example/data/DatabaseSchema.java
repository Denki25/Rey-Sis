package org.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class DatabaseSchema {
    private DatabaseSchema() {
    }

    public static void ensureReady(String jdbcUrl, String username, String password) throws SQLException {
        String databaseName = extractDatabaseName(jdbcUrl);
        if (databaseName == null || databaseName.isBlank()) {
            return;
        }

        String systemUrl = buildSystemDatabaseUrl(jdbcUrl);
        try (Connection connection = DriverManager.getConnection(systemUrl, username, password);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + databaseName + "`");
        }

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            ensureReady(connection);
        }
    }

    public static void ensureReady(Connection connection) throws SQLException {
        String schema = "CREATE TABLE IF NOT EXISTS users ("
                + "user_id INT NOT NULL AUTO_INCREMENT,"
                + "username VARCHAR(100) NOT NULL UNIQUE,"
                + "password_hash VARCHAR(255) NOT NULL,"
                + "role VARCHAR(30) NOT NULL,"
                + "is_active BOOLEAN NOT NULL DEFAULT TRUE,"
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (user_id)"
                + ");"
                + "CREATE TABLE IF NOT EXISTS students ("
                + "student_id VARCHAR(50) NOT NULL,"
                + "user_id INT NULL,"
                + "first_name VARCHAR(100) NOT NULL,"
                + "last_name VARCHAR(100) NOT NULL,"
                + "program VARCHAR(100) NOT NULL,"
                + "year_level VARCHAR(50) NOT NULL,"
                + "section VARCHAR(50) NULL,"
                + "email VARCHAR(150) NULL,"
                + "contact_number VARCHAR(50) NULL,"
                + "date_of_birth DATE NULL,"
                + "address TEXT NULL,"
                + "academic_status VARCHAR(50) NULL,"
                + "curriculum_year VARCHAR(50) NULL,"
                + "adviser VARCHAR(150) NULL,"
                + "avatar_path VARCHAR(255) NULL,"
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (student_id),"
                + "UNIQUE (user_id),"
                + "CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(user_id)"
                + ");"
                + "CREATE TABLE IF NOT EXISTS courses ("
                + "course_id INT NOT NULL AUTO_INCREMENT,"
                + "course_code VARCHAR(50) NOT NULL UNIQUE,"
                + "course_name VARCHAR(200) NOT NULL,"
                + "units INT NOT NULL DEFAULT 0,"
                + "department VARCHAR(100) NULL,"
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (course_id)"
                + ");"
                + "CREATE TABLE IF NOT EXISTS schedules ("
                + "schedule_id INT NOT NULL AUTO_INCREMENT,"
                + "course_id INT NOT NULL,"
                + "day_of_week VARCHAR(20) NOT NULL,"
                + "start_time TIME NOT NULL,"
                + "end_time TIME NOT NULL,"
                + "room VARCHAR(100) NULL,"
                + "instructor VARCHAR(150) NULL,"
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (schedule_id),"
                + "CONSTRAINT fk_schedules_course FOREIGN KEY (course_id) REFERENCES courses(course_id)"
                + ");"
                + "CREATE TABLE IF NOT EXISTS enrollments ("
                + "enrollment_id INT NOT NULL AUTO_INCREMENT,"
                + "student_id VARCHAR(50) NOT NULL,"
                + "course_id INT NOT NULL,"
                + "academic_year VARCHAR(20) NOT NULL,"
                + "semester VARCHAR(20) NOT NULL,"
                + "status VARCHAR(30) NOT NULL DEFAULT 'ENROLLED',"
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (enrollment_id),"
                + "CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id) REFERENCES students(student_id),"
                + "CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses(course_id)"
                + ");"
                + "CREATE TABLE IF NOT EXISTS grades ("
                + "grade_id INT NOT NULL AUTO_INCREMENT,"
                + "student_id VARCHAR(50) NOT NULL,"
                + "course_id INT NOT NULL,"
                + "semester VARCHAR(20) NOT NULL,"
                + "academic_year VARCHAR(20) NOT NULL,"
                + "grade DECIMAL(4,2) NULL,"
                + "remarks TEXT NULL,"
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (grade_id),"
                + "CONSTRAINT fk_grades_student FOREIGN KEY (student_id) REFERENCES students(student_id),"
                + "CONSTRAINT fk_grades_course FOREIGN KEY (course_id) REFERENCES courses(course_id)"
                + ");"
                + "CREATE TABLE IF NOT EXISTS announcements ("
                + "announcement_id INT NOT NULL AUTO_INCREMENT,"
                + "content TEXT NOT NULL,"
                + "published_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "is_active BOOLEAN NOT NULL DEFAULT TRUE,"
                + "PRIMARY KEY (announcement_id)"
                + ");"
                + "CREATE TABLE IF NOT EXISTS tasks ("
                + "task_id INT NOT NULL AUTO_INCREMENT,"
                + "student_id VARCHAR(50) NOT NULL,"
                + "title VARCHAR(200) NOT NULL,"
                + "due_date DATE NULL,"
                + "is_completed BOOLEAN NOT NULL DEFAULT FALSE,"
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (task_id),"
                + "CONSTRAINT fk_tasks_student FOREIGN KEY (student_id) REFERENCES students(student_id)"
                + ");"
                + "CREATE TABLE IF NOT EXISTS payments ("
                + "payment_id INT NOT NULL AUTO_INCREMENT,"
                + "student_id VARCHAR(50) NOT NULL,"
                + "amount DECIMAL(12,2) NOT NULL,"
                + "payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "payment_type VARCHAR(50) NOT NULL,"
                + "reference_number VARCHAR(100) NOT NULL UNIQUE,"
                + "status VARCHAR(30) NOT NULL DEFAULT 'VERIFIED',"
                + "PRIMARY KEY (payment_id),"
                + "CONSTRAINT fk_payments_student FOREIGN KEY (student_id) REFERENCES students(student_id)"
                + ");";

        try (Statement statement = connection.createStatement()) {
            for (String tableStatement : schema.split(";")) {
                String sql = tableStatement.trim();
                if (!sql.isEmpty()) {
                    statement.executeUpdate(sql);
                }
            }
        }
        seedDemoUsers(connection);
    }

    private static void seedDemoUsers(Connection connection) throws SQLException {
        String passwordHash = hashDefaultPassword();
        String sql = "INSERT INTO users (username, password_hash, role, is_active) VALUES (?, ?, ?, TRUE) "
                + "ON DUPLICATE KEY UPDATE password_hash = ?, role = ?, is_active = TRUE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String[] account : new String[][]{
                    {"student", "STUDENT"},
                    {"cashier", "CASHIER"},
                    {"admin", "ADMIN"}
            }) {
                statement.setString(1, account[0]);
                statement.setString(2, passwordHash);
                statement.setString(3, account[1]);
                statement.setString(4, passwordHash);
                statement.setString(5, account[1]);
                statement.executeUpdate();
            }
        }

        int studentUserId;
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT user_id FROM users WHERE username = 'student'");
             ResultSet result = statement.executeQuery()) {
            if (!result.next()) {
                throw new SQLException("The default student login could not be initialized.");
            }
            studentUserId = result.getInt("user_id");
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM students WHERE user_id = ?")) {
            statement.setInt(1, studentUserId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    insertDemoStudent(connection, studentUserId);
                }
            }
        }
    }

    private static void insertDemoStudent(Connection connection, int userId) throws SQLException {
        String studentId = "DEMO-" + userId;
        String sql = "INSERT INTO students (student_id, user_id, first_name, last_name, program, year_level, "
                + "section, email, academic_status) VALUES (?, ?, 'Demo', 'Student', 'BS Information Technology', "
                + "'1st Year', 'A', 'student@rey-sis.local', 'Active') "
                + "ON DUPLICATE KEY UPDATE user_id = VALUES(user_id)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    private static String hashDefaultPassword() throws SQLException {
        char[] password = "123".toCharArray();
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(password, salt, 120_000, 256);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return "pbkdf2$120000$" + Base64.getEncoder().encodeToString(salt)
                    + "$" + Base64.getEncoder().encodeToString(hash);
        } catch (GeneralSecurityException exception) {
            throw new SQLException("Unable to initialize default account credentials.", exception);
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(password, '\0');
        }
    }

    private static String extractDatabaseName(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return null;
        }
        String trimmed = jdbcUrl.trim();
        int protocolIndex = trimmed.indexOf("://");
        if (protocolIndex < 0) {
            return null;
        }
        String remainder = trimmed.substring(protocolIndex + 3);
        int slashIndex = remainder.indexOf('/');
        if (slashIndex < 0) {
            return null;
        }
        String databaseName = remainder.substring(slashIndex + 1);
        int queryIndex = databaseName.indexOf('?');
        if (queryIndex >= 0) {
            databaseName = databaseName.substring(0, queryIndex);
        }
        return databaseName.isBlank() ? null : databaseName;
    }

    private static String buildSystemDatabaseUrl(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return jdbcUrl;
        }
        int protocolIndex = jdbcUrl.indexOf("://");
        if (protocolIndex < 0) {
            return jdbcUrl;
        }
        String remainder = jdbcUrl.substring(protocolIndex + 3);
        int slashIndex = remainder.indexOf('/');
        if (slashIndex < 0) {
            return jdbcUrl;
        }
        return jdbcUrl.substring(0, protocolIndex + 3) + remainder.substring(0, slashIndex);
    }
}
