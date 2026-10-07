package org.example.service;

import org.example.auth.UserSession;
import org.example.data.PaymentRepository;

import java.sql.SQLException;
import java.util.List;

public final class CashierPaymentService {
    private final PaymentRepository paymentRepository;

    public CashierPaymentService() {
        this(new PaymentRepository());
    }

    public CashierPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<PaymentRepository.PaymentStudent> findStudentsWithBalances() throws SQLException {
        requireCashierOrAdmin();
        return paymentRepository.findStudentsWithBalances();
    }

    public void record(String studentId, double amount, String paymentType, String referenceNumber) throws SQLException {
        requireCashierOrAdmin();
        paymentRepository.record(studentId, amount, paymentType, referenceNumber);
    }

    public List<PaymentRepository.PaymentRecord> findRecent() throws SQLException {
        requireCashierOrAdmin();
        return paymentRepository.findRecent();
    }

    private void requireCashierOrAdmin() {
        if (UserSession.getCurrentUser() == null) {
            throw new SecurityException("An authenticated session is required.");
        }
        String role = UserSession.getCurrentUser().getRole();
        if (!"CASHIER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)
                && !"REGISTRAR".equalsIgnoreCase(role)) {
            throw new SecurityException("Cashier access is required.");
        }
    }
}
