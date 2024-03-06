package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AttackStepDAO {
    private static final String INSERT_ATTACK_STEP = "INSERT INTO AttackStep (step, phase, attackStepDescription, capecId) VALUES (?, ?, ?, ?)";

    public void saveAttackSteps(List<AttackStep> attackSteps) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ATTACK_STEP)) {

            for (AttackStep step : attackSteps) {
                preparedStatement.setString(1, step.getStep());
                preparedStatement.setString(2, step.getPhase());
                preparedStatement.setString(3, step.getAttackStepDescription());
                preparedStatement.setInt(4, step.getCapecId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
