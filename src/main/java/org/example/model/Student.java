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
    private final List<EnrollmentRecord> enrollments;
    private final List<GradeRecord> grades;
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
                   String contactNumber, int enrolledSubjects, double currentGpa, int enrolledUnits,
                   int maximumUnits, String deansListStanding, List<ScheduleItem> schedule,
                   List<Announcement> announcements, List<Task> tasks) {
        this(studentId, name, program, yearLevel, email, contactNumber, enrolledSubjects, currentGpa,
                enrolledUnits, maximumUnits, deansListStanding, schedule, announcements, tasks,
                null, null, null, null, null, null, null, null);
    }

    public Student(String studentId, String name, String program, String yearLevel, String email,
                   String contactNumber, int enrolledSubjects, double currentGpa, int enrolledUnits,
                   int maximumUnits, String deansListStanding, List<ScheduleItem> schedule,
                   List<Announcement> announcements, List<Task> tasks, String dateOfBirth,
                   String address, String section, String academicStatus, String curriculumYear,
                   String adviser, String avatarPath, String enrollmentSemester) {
        this(studentId, name, program, yearLevel, email, contactNumber, enrolledSubjects, currentGpa,
                enrolledUnits, maximumUnits, deansListStanding, schedule, announcements, tasks,
                dateOfBirth, address, section, academicStatus, curriculumYear, adviser, avatarPath,
                enrollmentSemester, List.of(), List.of());
    }

    public Student(String studentId, String name, String program, String yearLevel, String email,
                   String contactNumber, int enrolledSubjects, double currentGpa, int enrolledUnits,
                   int maximumUnits, String deansListStanding, List<ScheduleItem> schedule,
                   List<Announcement> announcements, List<Task> tasks, String dateOfBirth,
                   String address, String section, String academicStatus, String curriculumYear,
                   String adviser, String avatarPath, String enrollmentSemester,
                   List<EnrollmentRecord> enrollments, List<GradeRecord> grades) {
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
        this.grades = List.copyOf(grades);
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
    public String getEnrollmentSemester() { return enrollmentSemester; }
    public List<EnrollmentRecord> getEnrollments() { return enrollments; }
    public List<GradeRecord> getGrades() { return grades; }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; this.location = address; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
}
