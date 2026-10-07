package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class GradeRepository {
    public String[] findStudentLabels() throws SQLException {
        String sql = "SELECT student_id, CONCAT(first_name, ' ', last_name) AS student_name "
                + "FROM students ORDER BY last_name, first_name";
        java.util.List<String> values = new java.util.ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                values.add(result.getString("student_id") + " | " + result.getString("student_name"));
            }
        }
        return values.toArray(String[]::new);
    }

    public String[] findCourseLabels() throws SQLException {
        String sql = "SELECT course_code, course_name FROM courses ORDER BY course_code";
        java.util.List<String> values = new java.util.ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                values.add(result.getString("course_code") + " | " + result.getString("course_name"));
            }
        }
        return values.toArray(String[]::new);
    }

    public void save(String studentId, String courseCode, String semester, String academicYear,
                     double grade, String remarks) throws SQLException {
        if (grade < 1.0 || grade > 5.0) {
            throw new IllegalArgumentException("Grade must be between 1.00 and 5.00.");
        }
        String findCourse = "SELECT course_id FROM courses WHERE course_code = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement courseStatement = connection.prepareStatement(findCourse)) {
            courseStatement.setString(1, courseCode);
            try (ResultSet course = courseStatement.executeQuery()) {
                if (!course.next()) {
                    throw new SQLException("Course was not found.");
                }
                int courseId = course.getInt(1);
                String findGrade = "SELECT grade_id FROM grades WHERE student_id = ? AND course_id = ? "
                        + "AND semester = ? AND academic_year = ?";
                try (PreparedStatement gradeStatement = connection.prepareStatement(findGrade)) {
                    gradeStatement.setString(1, studentId);
                    gradeStatement.setInt(2, courseId);
                    gradeStatement.setString(3, semester);
                    gradeStatement.setString(4, academicYear);
                    try (ResultSet existing = gradeStatement.executeQuery()) {
                        if (existing.next()) {
                            String update = "UPDATE grades SET grade = ?, remarks = ? WHERE grade_id = ?";
                            try (PreparedStatement updateStatement = connection.prepareStatement(update)) {
                                updateStatement.setDouble(1, grade);
                                updateStatement.setString(2, remarks);
                                updateStatement.setInt(3, existing.getInt(1));
                                updateStatement.executeUpdate();
                            }
                        } else {
                            String insert = "INSERT INTO grades (student_id, course_id, semester, academic_year, grade, remarks) "
                                    + "VALUES (?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
                                insertStatement.setString(1, studentId);
                                insertStatement.setInt(2, courseId);
                                insertStatement.setString(3, semester);
                                insertStatement.setString(4, academicYear);
                                insertStatement.setDouble(5, grade);
                                insertStatement.setString(6, remarks);
                                insertStatement.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
    }
}
