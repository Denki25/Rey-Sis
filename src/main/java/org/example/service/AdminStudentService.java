package org.example.service;

import org.example.auth.UserSession;
import org.example.data.StudentDirectoryRepository;

import java.sql.SQLException;
import java.util.List;

public final class AdminStudentService {
    private final StudentDirectoryRepository repository;

    public AdminStudentService() {
        this(new StudentDirectoryRepository());
    }

    public AdminStudentService(StudentDirectoryRepository repository) {
        this.repository = repository;
    }

    public List<StudentDirectoryRepository.StudentSummary> findStudents() throws SQLException {
        requireRegistrar();
        return repository.findAll();
    }

    public StudentDirectoryRepository.CreatedStudent createStudent(String firstName, String lastName,
                                                                   String program, String yearLevel,
                                                                   String section, String email) throws SQLException {
        requireRegistrar();
        return repository.createStudent(firstName, lastName, program, yearLevel, section, email);
    }

    private void requireRegistrar() {
        String role = UserSession.getCurrentUser() == null ? "" : UserSession.getCurrentUser().getRole();
        if (!"ADMIN".equalsIgnoreCase(role) && !"REGISTRAR".equalsIgnoreCase(role)) {
            throw new SecurityException("Registrar access is required.");
        }
    }
}
