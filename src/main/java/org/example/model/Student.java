package org.example.model;

import java.util.List;

public final class Student {
    private final String studentId;
    private String name;
    private final String program;
    private final String yearLevel;
    private String email;
    private String contactNumber;
    private final int enrolledSubjects;
    private final double currentGpa;
    private final int enrolledUnits;
    private final int maximumUnits;
    private final String deansListStanding;
    private final List<ScheduleItem> schedule;
    private final List<Announcement> announcements;
    private final List<Task> tasks;
    private String dateOfBirth = "September 18, 2003";
    private String address = "San Fernando, Pampanga";
    private String location = "San Fernando, Pampanga";
    private String section = "BSIT 3-A";
    private String academicStatus = "Regular Student";
    private String curriculumYear = "2023 Curriculum";
    private String adviser = "Prof. Maria L. Santos";
    private String avatarPath;

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
    public String getDateOfBirth() { return dateOfBirth; }
    public String getAddress() { return address; }
    public String getLocation() { return location; }
    public String getSection() { return section; }
    public String getAcademicStatus() { return academicStatus; }
    public String getCurriculumYear() { return curriculumYear; }
    public String getAdviser() { return adviser; }
    public String getAvatarPath() { return avatarPath; }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; this.location = address; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
}
