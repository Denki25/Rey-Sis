package org.example.model;

public final class Announcement {
    private final String message;
    private final String date;

    public Announcement(String message, String date) {
        this.message = message;
        this.date = date;
    }

    public String getMessage() { return message; }
    public String getDate() { return date; }
}
