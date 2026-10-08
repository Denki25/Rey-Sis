package org.example.data;

import org.example.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class CourseRepository {
    public List<Course> findAll() throws SQLException {
        String sql = "SELECT course_code, course_name, units, department FROM courses ORDER BY course_code";
        List<Course> courses = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                courses.add(new Course(result.getString("course_code"), result.getString("course_name"),
                        result.getBigDecimal("units").intValue(), result.getString("department")));
            }
        }
        return courses;
    }

    public List<CourseSchedule> findSchedulesByCourseCode(String courseCode) throws SQLException {
        String sql = "SELECT s.schedule_id, s.day_of_week, s.start_time, s.end_time, s.room, s.instructor "
                + "FROM schedules s JOIN courses c ON c.course_id = s.course_id "
                + "WHERE c.course_code = ? ORDER BY s.day_of_week, s.start_time";
        List<CourseSchedule> schedules = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseCode);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    schedules.add(new CourseSchedule(result.getInt("schedule_id"), result.getString("day_of_week"),
                            result.getTime("start_time"), result.getTime("end_time"),
                            result.getString("room"), result.getString("instructor")));
                }
            }
        }
        return schedules;
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

    public void createSchedule(String courseCode, String dayOfWeek, LocalTime startTime, LocalTime endTime,
                               String room, String instructor) throws SQLException {
        String sql = "INSERT INTO schedules (course_id, day_of_week, start_time, end_time, room, instructor) "
                + "SELECT course_id, ?, ?, ?, ?, ? FROM courses WHERE course_code = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dayOfWeek);
            statement.setTime(2, Time.valueOf(startTime));
            statement.setTime(3, Time.valueOf(endTime));
            statement.setString(4, room);
            statement.setString(5, instructor);
            statement.setString(6, courseCode);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Course was not found; schedule was not created.");
            }
        }
    }

    public void updateSchedule(int scheduleId, String dayOfWeek, LocalTime startTime, LocalTime endTime,
                               String room, String instructor) throws SQLException {
        String sql = "UPDATE schedules SET day_of_week = ?, start_time = ?, end_time = ?, room = ?, instructor = ? "
                + "WHERE schedule_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dayOfWeek);
            statement.setTime(2, Time.valueOf(startTime));
            statement.setTime(3, Time.valueOf(endTime));
            statement.setString(4, room);
            statement.setString(5, instructor);
            statement.setInt(6, scheduleId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Schedule no longer exists.");
            }
        }
    }

    public void deleteSchedule(int scheduleId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM schedules WHERE schedule_id = ?")) {
            statement.setInt(1, scheduleId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Schedule no longer exists.");
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

    public record CourseSchedule(int scheduleId, String dayOfWeek, Time startTime, Time endTime,
                                 String room, String instructor) {
    }
}
