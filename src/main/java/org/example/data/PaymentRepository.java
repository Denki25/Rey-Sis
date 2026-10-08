package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PaymentRepository {
    public List<PaymentStudent> findStudentsWithBalances() throws SQLException {
        String sql = "SELECT s.student_id, CONCAT(s.first_name, ' ', s.last_name) AS student_name, s.program, "
                + "(COALESCE(e.enrolled_units, 0) * b.tuition_rate_per_unit + "
                + "CASE WHEN COALESCE(e.enrolled_units, 0) > 0 THEN "
                + "b.library_fee + b.registration_fee + b.it_lab_fee + b.athletics_fee ELSE 0 END) AS tuition_assessed, "
                + "COALESCE(p.verified_payments, 0) AS verified_payments, "
                + "GREATEST(COALESCE(e.enrolled_units, 0) * b.tuition_rate_per_unit + "
                + "CASE WHEN COALESCE(e.enrolled_units, 0) > 0 THEN "
                + "b.library_fee + b.registration_fee + b.it_lab_fee + b.athletics_fee ELSE 0 END "
                + "- COALESCE(p.verified_payments, 0), 0) AS tuition_balance "
                + "FROM students s CROSS JOIN billing_settings b "
                + "LEFT JOIN (SELECT e.student_id, SUM(c.units) AS enrolled_units FROM enrollments e "
                + "JOIN courses c ON c.course_id = e.course_id "
                + "WHERE e.status = 'ENROLLED' OR e.status IS NULL GROUP BY e.student_id) e "
                + "ON e.student_id = s.student_id "
                + "LEFT JOIN (SELECT student_id, SUM(amount) AS verified_payments FROM payments "
                + "WHERE status = 'VERIFIED' GROUP BY student_id) p ON p.student_id = s.student_id "
                + "WHERE b.settings_id = 1 ORDER BY s.last_name, s.first_name";
        List<PaymentStudent> students = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                students.add(new PaymentStudent(result.getString("student_id"), result.getString("student_name"),
                        result.getString("program"), result.getDouble("tuition_assessed"),
                        result.getDouble("verified_payments"), result.getDouble("tuition_balance")));
            }
        }
        return students;
    }

    public void record(String studentId, double amount, String paymentType, String referenceNumber) throws SQLException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive.");
        }
        String sql = "INSERT INTO payments (student_id, amount, payment_type, reference_number, status) "
                + "VALUES (?, ?, ?, ?, 'VERIFIED')";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setDouble(2, amount);
            statement.setString(3, paymentType);
            statement.setString(4, referenceNumber);
            statement.executeUpdate();
        }
    }

    public List<PaymentRecord> findRecent() throws SQLException {
        String sql = "SELECT p.reference_number, CONCAT(s.first_name, ' ', s.last_name) AS student_name, "
                + "s.program, s.student_id, p.amount, p.payment_type, p.status, p.payment_date "
                + "FROM payments p JOIN students s ON s.student_id = p.student_id ORDER BY p.payment_date DESC LIMIT 100";
        List<PaymentRecord> payments = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                payments.add(new PaymentRecord(result.getString("reference_number"), result.getString("student_name"),
                        result.getString("program") + " · " + result.getString("student_id"), result.getDouble("amount"),
                        result.getString("payment_type"), result.getString("status"), result.getTimestamp("payment_date")));
            }
        }
        return payments;
    }

    public List<PaymentRecord> findByStudentId(String studentId) throws SQLException {
        String sql = "SELECT p.reference_number, CONCAT(s.first_name, ' ', s.last_name) AS student_name, "
                + "s.program, s.student_id, p.amount, p.payment_type, p.status, p.payment_date "
                + "FROM payments p JOIN students s ON s.student_id = p.student_id "
                + "WHERE p.student_id = ? ORDER BY p.payment_date DESC";
        List<PaymentRecord> payments = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    payments.add(new PaymentRecord(result.getString("reference_number"),
                            result.getString("student_name"),
                            result.getString("program") + " · " + result.getString("student_id"),
                            result.getDouble("amount"), result.getString("payment_type"),
                            result.getString("status"), result.getTimestamp("payment_date")));
                }
            }
        }
        return payments;
    }

    public List<PaymentRecord> findPaymentsBetween(LocalDate startDate, LocalDate endDateExclusive) throws SQLException {
        String sql = "SELECT p.reference_number, CONCAT(s.first_name, ' ', s.last_name) AS student_name, "
                + "s.program, s.student_id, p.amount, p.payment_type, p.status, p.payment_date "
                + "FROM payments p JOIN students s ON s.student_id = p.student_id "
                + "WHERE p.payment_date >= ? AND p.payment_date < ? ORDER BY p.payment_date DESC";
        List<PaymentRecord> payments = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
            statement.setTimestamp(2, Timestamp.valueOf(endDateExclusive.atStartOfDay()));
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    payments.add(new PaymentRecord(result.getString("reference_number"),
                            result.getString("student_name"),
                            result.getString("program") + " · " + result.getString("student_id"),
                            result.getDouble("amount"), result.getString("payment_type"),
                            result.getString("status"), result.getTimestamp("payment_date")));
                }
            }
        }
        return payments;
    }

    public PaymentSummary findSummary() throws SQLException {
        String sql = "SELECT "
                + "COALESCE(SUM(CASE WHEN status = 'VERIFIED' AND payment_date >= CURRENT_DATE "
                + "AND payment_date < CURRENT_DATE + INTERVAL 1 DAY THEN amount ELSE 0 END), 0) AS today_total, "
                + "COALESCE(SUM(CASE WHEN status = 'VERIFIED' AND payment_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') "
                + "THEN amount ELSE 0 END), 0) AS month_total, "
                + "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count "
                + "FROM payments";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            if (!result.next()) {
                throw new SQLException("Payment summary could not be calculated.");
            }
            return new PaymentSummary(result.getDouble("today_total"), result.getDouble("month_total"),
                    result.getInt("pending_count"));
        }
    }

    public List<PaymentMethodTotal> findTodayTotalsByPaymentType() throws SQLException {
        String sql = "SELECT payment_type, COALESCE(SUM(amount), 0) AS total_amount "
                + "FROM payments WHERE status = 'VERIFIED' AND payment_date >= CURRENT_DATE "
                + "AND payment_date < CURRENT_DATE + INTERVAL 1 DAY "
                + "GROUP BY payment_type ORDER BY payment_type";
        List<PaymentMethodTotal> totals = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                totals.add(new PaymentMethodTotal(result.getString("payment_type"), result.getDouble("total_amount")));
            }
        }
        return totals;
    }

    public void voidVerifiedPayment(String referenceNumber) throws SQLException {
        String sql = "UPDATE payments SET status = 'VOIDED' WHERE reference_number = ? AND status = 'VERIFIED'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, referenceNumber);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Payment was not found or is not verified.");
            }
        }
    }

    public void updatePendingPaymentStatus(String referenceNumber, String newStatus) throws SQLException {
        if (!"VERIFIED".equals(newStatus) && !"REJECTED".equals(newStatus)) {
            throw new IllegalArgumentException("Unsupported payment status.");
        }
        String sql = "UPDATE payments SET status = ? WHERE reference_number = ? AND status = 'PENDING'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatus);
            statement.setString(2, referenceNumber);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Payment was not found or is no longer pending.");
            }
        }
    }

    public record PaymentStudent(String studentId, String name, String program, double tuitionAssessed,
                                 double verifiedPayments, double tuitionBalance) {
    }

    public record PaymentRecord(String referenceNumber, String studentName, String programId, double amount,
                                String paymentType, String status, Timestamp paymentDate) {
    }

    public record PaymentSummary(double todayTotal, double monthTotal, int pendingCount) {
    }

    public record PaymentMethodTotal(String paymentType, double totalAmount) {
    }
}
