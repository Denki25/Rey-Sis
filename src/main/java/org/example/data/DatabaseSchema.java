package org.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.security.GeneralSecurityException;
import java.time.Year;
import org.example.auth.PasswordHashing;

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
                + ");"
                + "CREATE TABLE IF NOT EXISTS billing_settings ("
                + "settings_id INT NOT NULL,"
                + "tuition_rate_per_unit DECIMAL(10,2) NOT NULL,"
                + "library_fee DECIMAL(10,2) NOT NULL,"
                + "registration_fee DECIMAL(10,2) NOT NULL,"
                + "it_lab_fee DECIMAL(10,2) NOT NULL,"
                + "athletics_fee DECIMAL(10,2) NOT NULL,"
                + "PRIMARY KEY (settings_id)"
                + ");"
                + "CREATE TABLE IF NOT EXISTS student_id_sequences ("
                + "sequence_year INT NOT NULL,"
                + "next_number INT NOT NULL,"
                + "PRIMARY KEY (sequence_year)"
                + ");";

        try (Statement statement = connection.createStatement()) {
            for (String tableStatement : schema.split(";")) {
                String sql = tableStatement.trim();
                if (!sql.isEmpty()) {
                    statement.executeUpdate(sql);
                }
            }
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO billing_settings "
                    + "(settings_id, tuition_rate_per_unit, library_fee, registration_fee, it_lab_fee, athletics_fee) "
                    + "VALUES (1, 1800.00, 1500.00, 1000.00, 2500.00, 800.00) "
                    + "ON DUPLICATE KEY UPDATE settings_id = VALUES(settings_id)");
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
                    {"student2", "STUDENT"},
                    {"student3", "STUDENT"},
                    {"student4", "STUDENT"},
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

        String[][] demos = {
                {"student", "Denric", "Mendoza", "Bachelor of Science in Information Technology", "2nd Year"},
                {"student2", "Maria", "Santos", "Bachelor of Science in Computer Science", "1st Year"},
                {"student3", "Joshua", "Reyes", "Bachelor of Science in Information Technology", "3rd Year"},
                {"student4", "Angela", "Cruz", "Bachelor of Science in Information Systems", "2nd Year"}
        };
        for (String[] demo : demos) {
            int userId = findUserId(connection, demo[0]);
            if (!hasStudentProfile(connection, userId)) {
                int year = Year.now().getValue();
                String studentId = nextStudentId(connection, year);
                insertStudent(connection, studentId, userId, demo[1], demo[2], demo[3], demo[4],
                        "A", demo[0] + "@rey-sis.local");
            }
        }
    }

    private static int findUserId(Connection connection, String username) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT user_id FROM users WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException("Demo account could not be initialized: " + username);
                }
                return result.getInt("user_id");
            }
        }
    }

    private static boolean hasStudentProfile(Connection connection, int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM students WHERE user_id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    static String nextStudentId(Connection connection, int year) throws SQLException {
        int nextNumber = 1;
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COALESCE(MAX(CAST(SUBSTRING(student_id, 6) AS UNSIGNED)), 0) + 1 "
                        + "FROM students WHERE student_id LIKE ?")) {
            statement.setString(1, year + "-%");
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    nextNumber = result.getInt(1);
                }
            }
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT IGNORE INTO student_id_sequences (sequence_year, next_number) VALUES (?, ?)")) {
            statement.setInt(1, year);
            statement.setInt(2, nextNumber);
            statement.executeUpdate();
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT next_number FROM student_id_sequences WHERE sequence_year = ? FOR UPDATE")) {
            statement.setInt(1, year);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException("Unable to reserve the next student ID.");
                }
                nextNumber = result.getInt("next_number");
            }
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE student_id_sequences SET next_number = ? WHERE sequence_year = ?")) {
            statement.setInt(1, nextNumber + 1);
            statement.setInt(2, year);
            statement.executeUpdate();
        }
        return "%d-%04d".formatted(year, nextNumber);
    }

    static void insertStudent(Connection connection, String studentId, int userId, String firstName,
                              String lastName, String program, String yearLevel, String section,
                              String email) throws SQLException {
        String sql = "INSERT INTO students (student_id, user_id, first_name, last_name, program, year_level, "
                + "section, email, academic_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Active')";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setInt(2, userId);
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setString(5, program);
            statement.setString(6, yearLevel);
            statement.setString(7, section);
            statement.setString(8, email);
            statement.executeUpdate();
        }
    }

    private static String hashDefaultPassword() throws SQLException {
        char[] password = "123".toCharArray();
        try {
            return PasswordHashing.hash(password);
        } catch (GeneralSecurityException exception) {
            throw new SQLException("Unable to initialize default account credentials.", exception);
        } finally {
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
