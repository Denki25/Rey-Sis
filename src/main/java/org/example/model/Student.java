package org.example.model;

import java.util.List;

public final class Student {
    private final String studentId;
    private final String name;
    private final String program;
    private final String yearLevel;
    private final String email;
    private final String contactNumber;
    private final int enrolledSubjects;
    private final double currentGpa;
    private final int enrolledUnits;
    private final int maximumUnits;
    private final String deansListStanding;
    private final List<ScheduleItem> schedule;
    private final List<Announcement> announcements;
    private final List<Task> tasks;

    public Student(String studentId, String name, String program, String yearLevel, String email,
                   String contactNumber, int enrolledSubjects, double currentGpa, int enrolledUnits,
                   int maximumUnits, String deansListStanding, List<ScheduleItem> schedule,
                   List<Announcement> announcements, List<Task> tasks) {
        this.studentId = studentId;
        this.name = name;
        this.program = program;
        this.yearLevel = yearLevel;
        this.email = email;
        this.contactNumber = contactNumber;
        this.enrolledSubjects = enrolledSubjects;
        this.currentGpa = currentGpa;
        this.enrolledUnits = enrolledUnits;
        this.maximumUnits = maximumUnits;
        this.deansListStanding = deansListStanding;
        this.schedule = List.copyOf(schedule);
        this.announcements = List.copyOf(announcements);
        this.tasks = List.copyOf(tasks);
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getProgram() { return program; }
    public String getYearLevel() { return yearLevel; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public int getEnrolledSubjects() { return enrolledSubjects; }
    public double getCurrentGpa() { return currentGpa; }
    public int getEnrolledUnits() { return enrolledUnits; }
    public int getMaximumUnits() { return maximumUnits; }
    public String getDeansListStanding() { return deansListStanding; }
    public List<ScheduleItem> getSchedule() { return schedule; }
    public List<Announcement> getAnnouncements() { return announcements; }
    public List<Task> getTasks() { return tasks; }
}
