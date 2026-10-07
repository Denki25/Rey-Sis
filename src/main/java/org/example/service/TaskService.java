package org.example.service;

import org.example.data.TaskRepository;

import java.sql.SQLException;

public final class TaskService {
    private final TaskRepository taskRepository;

    public TaskService() {
        this(new TaskRepository());
    }

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public int addTask(String studentId, String title) throws SQLException {
        return taskRepository.insert(studentId, title);
    }

    public void deleteTask(String studentId, int taskId) throws SQLException {
        taskRepository.delete(taskId, studentId);
    }

    public void setCompleted(String studentId, int taskId, boolean completed) throws SQLException {
        taskRepository.updateCompleted(taskId, studentId, completed);
    }
}
