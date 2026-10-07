package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public record StudentSummary(String id, String name, String program, String yearLevel, String enrollmentStatus) {
    }
}
