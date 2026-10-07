package org.example.data;

import org.example.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class CourseRepository {
    public List<Course> findAll() throws SQLException {
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

    public void create(Course course, String department) throws SQLException {
        String sql = "INSERT INTO courses (course_code, course_name, units, department) VALUES (?, ?, ?, ?)";
        executeUpdate(sql, course, department, null);
    }

    public void update(String oldCode, Course course, String department) throws SQLException {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, units = ?, department = ? WHERE course_code = ?";
        executeUpdate(sql, course, department, oldCode);
    }

    public void delete(String courseCode) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_code = ? AND NOT EXISTS "
                + "(SELECT 1 FROM enrollments e WHERE e.course_id = courses.course_id)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseCode);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Course does not exist or is already referenced.");
            }
        }
    }

    private void executeUpdate(String sql, Course course, String department, String oldCode) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, course.getCode());
            statement.setString(2, course.getTitle());
            statement.setInt(3, course.getUnits());
            statement.setString(4, department);
            if (oldCode != null) {
                statement.setString(5, oldCode);
            }
            statement.executeUpdate();
        }
    }
}
