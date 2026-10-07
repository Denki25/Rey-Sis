package org.example.service;

import org.example.auth.UserSession;
import org.example.data.GradeRepository;

import java.sql.SQLException;

public final class TeacherGradeService {
    private final GradeRepository gradeRepository;

    public TeacherGradeService() {
        this(new GradeRepository());
    }

    public TeacherGradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public String[] students() throws SQLException {
        requireTeacher();
        return gradeRepository.findStudentLabels();
    }

    public String[] courses() throws SQLException {
        requireTeacher();
        return gradeRepository.findCourseLabels();
    }

    public void saveGrade(String studentId, String courseCode, String semester, String academicYear,
                          double grade, String remarks) throws SQLException {
        requireTeacher();
        gradeRepository.save(studentId, courseCode, semester, academicYear, grade, remarks);
    }

    private void requireTeacher() {
        if (UserSession.getCurrentUser() == null || !"TEACHER".equalsIgnoreCase(UserSession.getCurrentUser().getRole())) {
            throw new SecurityException("Teacher access is required.");
        }
    }
}
