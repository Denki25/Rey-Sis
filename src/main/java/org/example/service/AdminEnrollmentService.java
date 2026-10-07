package org.example.service;

import org.example.auth.UserSession;
import org.example.data.AdminEnrollmentRepository;

import java.sql.SQLException;
import java.util.List;

public final class AdminEnrollmentService {
    private final AdminEnrollmentRepository repository;

    public AdminEnrollmentService() {
        this(new AdminEnrollmentRepository());
    }

    public AdminEnrollmentService(AdminEnrollmentRepository repository) {
        this.repository = repository;
    }

    public List<AdminEnrollmentRepository.EnrollmentSummary> findEnrollments() throws SQLException {
        String role = UserSession.getCurrentUser() == null ? "" : UserSession.getCurrentUser().getRole();
        if (!"ADMIN".equalsIgnoreCase(role) && !"REGISTRAR".equalsIgnoreCase(role)) {
            throw new SecurityException("Admin access is required.");
        }
        return repository.findAll();
    }
}
