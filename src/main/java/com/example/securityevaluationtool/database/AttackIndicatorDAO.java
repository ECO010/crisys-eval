package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttackIndicatorDAO {
    private static final String INSERT_INDICATORS = "INSERT INTO AttackIndicator (AttackIndicator, capecId) VALUES (?, ?)";

    public void saveIndicators (List<AttackIndicator> attackIndicators) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INDICATORS)) {

            for (AttackIndicator attackIndicator : attackIndicators) {
                preparedStatement.setString(1, attackIndicator.getIndicator());
                preparedStatement.setInt(2, attackIndicator.getCapecId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
