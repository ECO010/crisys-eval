package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EvaluationDAO {

    private static final String ADD_EVALUATION = "INSERT OR IGNORE INTO Evaluation (SystemName, EvalDT) VALUES (?, ?)";
    private static final String GET_LATEST_EVAL_ID = "SELECT EvaluationID FROM Evaluation ORDER BY EvaluationID DESC LIMIT 1";

    public void saveEvaluation(Evaluation evaluation) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_EVALUATION)) {
                preparedStatement.setString(1, evaluation.getCriticalSystemName());
                preparedStatement.setString(2, evaluation.getEvaluationDate());
                preparedStatement.addBatch();
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public int getLatestEvalID() {
        int evaluationID = 0;

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_LATEST_EVAL_ID)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                evaluationID = resultSet.getInt("EvaluationID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return evaluationID;
    }
}
