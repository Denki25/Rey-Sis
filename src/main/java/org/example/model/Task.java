package org.example.model;

public final class Task {
    private final int taskId;
    private final String title;
    private final String dueDate;
    private final boolean completed;

    public Task(String title, String dueDate) {
        this(0, title, dueDate, false);
    }

    public Task(int taskId, String title, String dueDate, boolean completed) {
        this.taskId = taskId;
        this.title = title;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public int getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
}
