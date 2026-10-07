package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DashboardStatisticsRepository {
    public DashboardStatistics find() throws SQLException {
        String sql = "SELECT (SELECT COUNT(*) FROM students) AS total_students, "
                + "(SELECT COUNT(DISTINCT student_id) FROM enrollments WHERE status = 'ENROLLED' OR status IS NULL) AS enrolled_students, "
                + "(SELECT COUNT(*) FROM courses) AS total_courses, "
                + "(SELECT COUNT(*) FROM users WHERE is_active = TRUE) AS active_users";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return new DashboardStatistics(result.getInt("total_students"), result.getInt("enrolled_students"),
                    result.getInt("total_courses"), result.getInt("active_users"));
        }
    }

    public record DashboardStatistics(int totalStudents, int enrolledStudents, int totalCourses, int activeUsers) {
    }
}
