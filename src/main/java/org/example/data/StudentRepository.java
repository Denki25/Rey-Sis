package org.example.data;

import org.example.model.Announcement;
import org.example.model.Course;
import org.example.model.EnrollmentRecord;
import org.example.model.GradeRecord;
import org.example.model.ScheduleItem;
import org.example.model.Student;
import org.example.model.Task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class StudentRepository {
    private static final int DEFAULT_MAXIMUM_UNITS = 18;
    private final TaskRepository taskRepository;

    public StudentRepository() {
        this(new TaskRepository());
    }

    public StudentRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Student findByUserId(int userId) throws SQLException {
        String sql = "SELECT student_id, first_name, last_name, program, year_level, section, "
                + "email, contact_number, date_of_birth, address, academic_status, curriculum_year, "
                + "adviser, avatar_path FROM students WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                String studentId = result.getString("student_id");
                List<EnrollmentRecord> enrollments = findEnrollments(connection, studentId);
                List<GradeRecord> grades = findGrades(connection, studentId);
                AcademicSummary summary = summarize(enrollments, grades);
                Date dateOfBirth = result.getDate("date_of_birth");
                String fullName = (result.getString("first_name") + " " + result.getString("last_name")).trim();
                return new Student(studentId, fullName, result.getString("program"), result.getString("year_level"),
                        result.getString("email"), result.getString("contact_number"), summary.subjectCount,
                        summary.gpa, summary.units, DEFAULT_MAXIMUM_UNITS, summary.standing,
                        findSchedule(connection, studentId), findAnnouncements(connection),
                        taskRepository.findByStudentId(studentId), formatDate(dateOfBirth), result.getString("address"),
                        result.getString("section"), result.getString("academic_status"),
                        result.getString("curriculum_year"), result.getString("adviser"),
                        result.getString("avatar_path"), summary.enrollmentSemester, enrollments, grades);
            }
        }
    }

    private List<EnrollmentRecord> findEnrollments(Connection connection, String studentId) throws SQLException {
        String sql = "SELECT e.enrollment_id, c.course_code, c.course_name, c.units, e.academic_year, "
                + "e.semester, e.status FROM enrollments e JOIN courses c ON c.course_id = e.course_id "
                + "WHERE e.student_id = ? ORDER BY e.academic_year DESC, e.semester, c.course_code";
        List<EnrollmentRecord> enrollments = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Course course = new Course(result.getString("course_code"), result.getString("course_name"),
                            result.getBigDecimal("units").intValue());
                    enrollments.add(new EnrollmentRecord(result.getInt("enrollment_id"), course,
                            result.getString("academic_year"), result.getString("semester"), result.getString("status")));
                }
            }
        }
        return enrollments;
    }

    private List<GradeRecord> findGrades(Connection connection, String studentId) throws SQLException {
        String sql = "SELECT c.course_code, c.course_name, c.units, g.grade, g.remarks, g.semester, "
                + "g.academic_year FROM grades g JOIN courses c ON c.course_id = g.course_id "
                + "WHERE g.student_id = ? ORDER BY g.academic_year DESC, g.semester, c.course_code";
        List<GradeRecord> grades = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    grades.add(new GradeRecord(
                            new Course(result.getString("course_code"), result.getString("course_name"),
                                    result.getBigDecimal("units").intValue()),
                            result.getBigDecimal("grade").doubleValue(), result.getString("remarks"),
                            result.getString("semester"), result.getString("academic_year")));
                }
            }
        }
        return grades;
    }

    private AcademicSummary summarize(List<EnrollmentRecord> enrollments, List<GradeRecord> grades) {
        int enrolledSubjects = (int) enrollments.stream()
                .filter(enrollment -> enrollment.status() == null || "ENROLLED".equalsIgnoreCase(enrollment.status()))
                .count();
        int enrolledUnits = enrollments.stream()
                .filter(enrollment -> enrollment.status() == null || "ENROLLED".equalsIgnoreCase(enrollment.status()))
                .mapToInt(enrollment -> enrollment.course().getUnits()).sum();
        double weightedTotal = grades.stream().mapToDouble(grade -> grade.grade() * grade.course().getUnits()).sum();
        int gradedUnits = grades.stream().mapToInt(grade -> grade.course().getUnits()).sum();
        double gpa = gradedUnits == 0 ? 0.0 : weightedTotal / gradedUnits;
        String standing = grades.isEmpty() ? "N/A" : gpa <= 1.75 ? "Eligible" : "Not Eligible";
        String semester = enrollments.isEmpty() ? null
                : enrollments.get(0).semester() + ", AY " + enrollments.get(0).academicYear();
        return new AcademicSummary(enrolledSubjects, gpa, enrolledUnits, standing, semester);
    }

    private List<ScheduleItem> findSchedule(Connection connection, String studentId) throws SQLException {
        String sql = "SELECT c.course_code, c.course_name, s.day_of_week, s.start_time, s.end_time, "
                + "s.room, s.instructor FROM enrollments e JOIN courses c ON c.course_id = e.course_id "
                + "JOIN schedules s ON s.course_id = c.course_id WHERE e.student_id = ? "
                + "AND (e.status = 'ENROLLED' OR e.status IS NULL) ORDER BY s.day_of_week, s.start_time";
        List<ScheduleItem> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    items.add(new ScheduleItem(new Course(result.getString("course_code"), result.getString("course_name")),
                            result.getString("day_of_week"), formatTime(result.getTime("start_time"))
                                    + " - " + formatTime(result.getTime("end_time")),
                            result.getString("room"), result.getString("instructor")));
                }
            }
        }
        return items;
    }

    private List<Announcement> findAnnouncements(Connection connection) throws SQLException {
        String sql = "SELECT content, published_at FROM announcements WHERE is_active = TRUE "
                + "ORDER BY published_at DESC LIMIT 5";
        List<Announcement> announcements = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                java.sql.Timestamp publishedAt = result.getTimestamp("published_at");
                announcements.add(new Announcement(result.getString("content"),
                        publishedAt == null ? "" : publishedAt.toLocalDateTime().toLocalDate().toString()));
            }
        }
        return announcements;
    }

    private static String formatDate(Date date) {
        return date == null ? "" : date.toLocalDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    }

    private static String formatTime(Time time) {
        return time == null ? "" : time.toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    private record AcademicSummary(int subjectCount, double gpa, int units, String standing, String enrollmentSemester) {
    }
}
