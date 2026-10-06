package org.example.model;

public final class Task {
    private final String title;
    private final String dueDate;

    public Task(String title, String dueDate) {
        this.title = title;
        this.dueDate = dueDate;
    }

    public String getTitle() { return title; }
    public String getDueDate() { return dueDate; }
}
