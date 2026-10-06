package org.example.model;

public final class ScheduleItem {
    private final Course course;
    private final String time;
    private final String room;
    private final String instructor;

    public ScheduleItem(Course course, String time, String room, String instructor) {
        this.course = course;
        this.time = time;
        this.room = room;
        this.instructor = instructor;
    }

    public Course getCourse() { return course; }
    public String getTime() { return time; }
    public String getRoom() { return room; }
    public String getInstructor() { return instructor; }
}
