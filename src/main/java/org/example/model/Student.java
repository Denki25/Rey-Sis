package org.example.model;

import java.util.List;

public final class Student extends Person {
    private final String program;
    private final String yearLevel;
    private String email;
    private String contactNumber;
    private final int enrolledSubjects;
    private final int enrolledUnits;
    private final int maximumUnits;
    private final List<ScheduleItem> schedule;
    private final List<Announcement> announcements;
    private final List<Task> tasks;
    private final List<EnrollmentRecord> enrollments;
    private String dateOfBirth;
    private String address;
    private String location;
    private final String section;
    private final String academicStatus;
    private final String curriculumYear;
    private final String adviser;
    private String avatarPath;
    private final String enrollmentSemester;

    public Student(String studentId, String name, String program, String yearLevel, String email,
                   String contactNumber, int enrolledSubjects, int enrolledUnits,
                   int maximumUnits, List<ScheduleItem> schedule,
                   List<Announcement> announcements, List<Task> tasks, String dateOfBirth,
                   String address, String section, String academicStatus, String curriculumYear,
                   String adviser, String avatarPath, String enrollmentSemester,
                   List<EnrollmentRecord> enrollments) {
        super(studentId, name);
        this.program = program;
        this.yearLevel = yearLevel;
        this.email = email;
        this.contactNumber = contactNumber;
        this.enrolledSubjects = enrolledSubjects;
        this.enrolledUnits = enrolledUnits;
        this.maximumUnits = maximumUnits;
        this.schedule = List.copyOf(schedule);
        this.announcements = List.copyOf(announcements);
        this.tasks = List.copyOf(tasks);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.location = address;
        this.section = section;
        this.academicStatus = academicStatus;
        this.curriculumYear = curriculumYear;
        this.adviser = adviser;
        this.avatarPath = avatarPath;
        this.enrollmentSemester = enrollmentSemester;
        this.enrollments = List.copyOf(enrollments);
    }

    public String getStudentId() { return getIdentifier(); }
    public String getProgram() { return program; }
    public String getYearLevel() { return yearLevel; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public int getEnrolledSubjects() { return enrolledSubjects; }
    public int getEnrolledUnits() { return enrolledUnits; }
    public int getMaximumUnits() { return maximumUnits; }
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
    public String getEnrollmentSemester() { return enrollmentSemester; }
    public List<EnrollmentRecord> getEnrollments() { return enrollments; }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    @Override
    public void setName(String name) { super.setName(name); }
    public void setAddress(String address) { this.address = address; this.location = address; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    @Override
    public String getRoleLabel() {
        return "Student";
    }
}
