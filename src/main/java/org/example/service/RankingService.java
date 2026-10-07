package org.example.service;

import org.example.auth.UserSession;
import org.example.data.RankingRepository;

import java.sql.SQLException;
import java.util.List;

public final class RankingService {
    private final RankingRepository repository;

    public RankingService() {
        this(new RankingRepository());
    }

    public RankingService(RankingRepository repository) {
        this.repository = repository;
    }

    public List<RankingRepository.RankingEntry> findTop(int limit) throws SQLException {
        String role = UserSession.getCurrentUser() == null ? "" : UserSession.getCurrentUser().getRole();
        if (!"ADMIN".equalsIgnoreCase(role) && !"REGISTRAR".equalsIgnoreCase(role)) {
            throw new SecurityException("Registrar access is required.");
        }
        return repository.findTop(limit);
    }
}
