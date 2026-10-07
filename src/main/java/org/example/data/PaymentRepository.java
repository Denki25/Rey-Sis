package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class PaymentRepository {
    public List<PaymentStudent> findStudentsWithBalances() throws SQLException {
        String sql = "SELECT s.student_id, CONCAT(s.first_name, ' ', s.last_name) AS student_name, s.program, "
                + "GREATEST(COALESCE(SUM(c.units) * 1800, 0) - COALESCE((SELECT SUM(p.amount) FROM payments p "
                + "WHERE p.student_id = s.student_id AND p.status = 'VERIFIED'), 0), 0) AS tuition_balance "
                + "FROM students s LEFT JOIN enrollments e ON e.student_id = s.student_id "
                + "LEFT JOIN courses c ON c.course_id = e.course_id GROUP BY s.student_id, s.first_name, s.last_name, s.program "
                + "ORDER BY s.last_name, s.first_name";
        List<PaymentStudent> students = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                students.add(new PaymentStudent(result.getString("student_id"), result.getString("student_name"),
                        result.getString("program"), result.getDouble("tuition_balance")));
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

    public record PaymentStudent(String studentId, String name, String program, double tuitionBalance) {
    }

    public record PaymentRecord(String referenceNumber, String studentName, String programId, double amount,
                                String paymentType, String status, Timestamp paymentDate) {
    }
}
