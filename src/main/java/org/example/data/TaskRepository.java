package org.example.data;

import org.example.model.Task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class TaskRepository {
    public List<Task> findByStudentId(String studentId) throws SQLException {
        String sql = "SELECT task_id, title, due_date, is_completed FROM tasks "
                + "WHERE student_id = ? ORDER BY created_at, task_id";
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Date dueDate = result.getDate("due_date");
                    tasks.add(new Task(result.getInt("task_id"), result.getString("title"),
                            dueDate == null ? "" : dueDate.toString(), result.getBoolean("is_completed")));
                }
            }
        }
        return tasks;
    }

    public int insert(String studentId, String title) throws SQLException {
        String sql = "INSERT INTO tasks (student_id, title, is_completed) VALUES (?, ?, FALSE)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, studentId);
            statement.setString(2, title);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("The task ID was not returned after insertion.");
                }
                return keys.getInt(1);
            }
        }
    }

    public void delete(int taskId, String studentId) throws SQLException {
        String sql = "DELETE FROM tasks WHERE task_id = ? AND student_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            statement.setString(2, studentId);
            statement.executeUpdate();
        }
    }

    public void updateCompleted(int taskId, String studentId, boolean completed) throws SQLException {
        String sql = "UPDATE tasks SET is_completed = ? WHERE task_id = ? AND student_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, completed);
            statement.setInt(2, taskId);
            statement.setString(3, studentId);
            statement.executeUpdate();
        }
    }
}
