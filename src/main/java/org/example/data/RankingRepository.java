package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class RankingRepository {
    public List<RankingEntry> findTop(int limit) throws SQLException {
        String sql = "SELECT s.student_id, CONCAT(s.first_name, ' ', s.last_name) AS student_name, s.program, "
                + "SUM(g.grade * c.units) / NULLIF(SUM(c.units), 0) AS gpa "
                + "FROM grades g JOIN students s ON s.student_id = g.student_id JOIN courses c ON c.course_id = g.course_id "
                + "WHERE g.grade IS NOT NULL GROUP BY s.student_id, s.first_name, s.last_name, s.program "
                + "ORDER BY gpa ASC LIMIT ?";
        List<RankingEntry> entries = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Math.max(1, limit));
            try (ResultSet result = statement.executeQuery()) {
                int rank = 1;
                while (result.next()) {
                    entries.add(new RankingEntry(rank++, result.getString("student_id"), result.getString("student_name"),
                            result.getString("program"), result.getDouble("gpa")));
                }
            }
        }
        return entries;
    }

    public record RankingEntry(int rank, String studentId, String studentName, String program, double gpa) {
    }
}
