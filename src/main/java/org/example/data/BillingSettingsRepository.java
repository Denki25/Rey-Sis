package org.example.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class BillingSettingsRepository {
    public BillingSettings findSettings() throws SQLException {
        String sql = "SELECT tuition_rate_per_unit, library_fee, registration_fee, it_lab_fee, athletics_fee "
                + "FROM billing_settings WHERE settings_id = 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            if (!result.next()) {
                throw new SQLException("Billing settings are not configured.");
            }
            return new BillingSettings(result.getDouble("tuition_rate_per_unit"),
                    result.getDouble("library_fee"), result.getDouble("registration_fee"),
                    result.getDouble("it_lab_fee"), result.getDouble("athletics_fee"));
        }
    }

    public record BillingSettings(double tuitionRatePerUnit, double libraryFee,
                                  double registrationFee, double itLabFee, double athleticsFee) {
        public double totalMiscellaneousFees() {
            return libraryFee + registrationFee + itLabFee + athleticsFee;
        }

        public double tuitionForUnits(int units) {
            return units * tuitionRatePerUnit;
        }
    }
}
