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
        String role = UserSession.getCurrentUser() == null ? "" : UserSession.getCurrentUser().getRole();
        if (!"ADMIN".equalsIgnoreCase(role) && !"REGISTRAR".equalsIgnoreCase(role)) {
            throw new SecurityException("Registrar access is required.");
        }
        return repository.findAll();
    }
}
