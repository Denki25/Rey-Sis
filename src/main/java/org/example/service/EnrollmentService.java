package org.example.service;

import org.example.data.DatabaseConnection;
import org.example.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class EnrollmentService {
    public List<Course> findCourses() throws SQLException {
        String sql = "SELECT course_code, course_name, units FROM courses ORDER BY course_code";
        List<Course> courses = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                courses.add(new Course(result.getString("course_code"), result.getString("course_name"),
                        result.getBigDecimal("units").intValue()));
            }
        }
        return courses;
    }

    public void enroll(String studentId, String courseCode, String academicYear, String semester) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, academic_year, semester, status) "
                + "SELECT ?, c.course_id, ?, ?, 'ENROLLED' FROM courses c "
                + "WHERE c.course_code = ? AND NOT EXISTS (SELECT 1 FROM enrollments e "
                + "WHERE e.student_id = ? AND e.course_id = c.course_id AND e.academic_year = ? AND e.semester = ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setString(2, academicYear);
            statement.setString(3, semester);
            statement.setString(4, courseCode);
            statement.setString(5, studentId);
            statement.setString(6, academicYear);
            statement.setString(7, semester);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Course is unavailable or already enrolled.");
            }
        }
    }

    public void drop(String studentId, int enrollmentId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ? AND student_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, enrollmentId);
            statement.setString(2, studentId);
            statement.executeUpdate();
        }
    }
}
