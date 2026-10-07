package org.example.service;

import org.example.auth.UserSession;
import org.example.data.PaymentRepository;

import java.sql.SQLException;
import java.time.LocalDate;
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

    public List<PaymentRepository.PaymentRecord> findByStudentId(String studentId) throws SQLException {
        requireCashierOrAdmin();
        return paymentRepository.findByStudentId(studentId);
    }

    public List<PaymentRepository.PaymentRecord> findPaymentsBetween(LocalDate startDate, LocalDate endDateExclusive)
            throws SQLException {
        requireCashierOrAdmin();
        return paymentRepository.findPaymentsBetween(startDate, endDateExclusive);
    }

    public PaymentRepository.PaymentSummary findSummary() throws SQLException {
        requireCashierOrAdmin();
        return paymentRepository.findSummary();
    }

    public List<PaymentRepository.PaymentMethodTotal> findTodayTotalsByPaymentType() throws SQLException {
        requireCashierOrAdmin();
        return paymentRepository.findTodayTotalsByPaymentType();
    }

    public void voidVerifiedPayment(String referenceNumber) throws SQLException {
        requireCashierOrAdmin();
        paymentRepository.voidVerifiedPayment(referenceNumber);
    }

    public void updatePendingPaymentStatus(String referenceNumber, String newStatus) throws SQLException {
        requireCashierOrAdmin();
        paymentRepository.updatePendingPaymentStatus(referenceNumber, newStatus);
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
