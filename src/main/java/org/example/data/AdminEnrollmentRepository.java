package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class AdminEnrollmentRepository {
    public List<EnrollmentSummary> findAll() throws SQLException {
        String sql = "SELECT e.enrollment_id, e.student_id, "
                + "CONCAT(s.first_name, ' ', s.last_name) AS student_name, s.program, s.year_level, "
                + "c.course_code, c.course_name, c.units, e.academic_year, e.semester, e.status "
                + "FROM enrollments e JOIN students s ON s.student_id = e.student_id "
                + "JOIN courses c ON c.course_id = e.course_id "
                + "ORDER BY e.academic_year DESC, e.semester, s.last_name, c.course_code";
        List<EnrollmentSummary> enrollments = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                enrollments.add(new EnrollmentSummary(
                        result.getInt("enrollment_id"),
                        result.getString("student_id"),
                        result.getString("student_name"),
                        result.getString("program"),
                        result.getString("year_level"),
                        result.getString("course_code"),
                        result.getString("course_name"),
                        result.getInt("units"),
                        result.getString("academic_year"),
                        result.getString("semester"),
                        result.getString("status")));
            }
        }
        return enrollments;
    }

    public record EnrollmentSummary(int enrollmentId, String studentId, String studentName, String program,
                                    String yearLevel, String courseCode, String courseName, int units,
                                    String academicYear, String semester, String status) {
    }
}
