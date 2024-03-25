package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EvaluationDAO {

    private static final String ADD_EVALUATION = "INSERT OR IGNORE INTO Evaluation (SystemName, EvalDT) VALUES (?, ?)";
    private static final String ADD_EVALUATION_ASSET = "INSERT OR IGNORE INTO EvaluationAsset (EvaluationID, AssetName, AssetType) VALUES (?, ?, ?)";
    private static final String GET_LATEST_EVAL_ID = "SELECT EvaluationID FROM Evaluation ORDER BY EvaluationID DESC LIMIT 1";
    private static final String GET_ASSET_TYPE = "SELECT AssetType FROM EvaluationAsset WHERE EvaluationID = ? AND AssetName = ?";

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

    public void saveEvaluationAssets(List<EvaluationAsset> evaluationAssets) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_EVALUATION_ASSET)) {

            for (EvaluationAsset evaluationAsset: evaluationAssets) {
                preparedStatement.setInt(1, evaluationAsset.getEvaluationID());
                preparedStatement.setString(2, evaluationAsset.getAssetName());
                preparedStatement.setString(3, evaluationAsset.getAssetType());
                preparedStatement.addBatch();
            }

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

    public String getAssetTypeFromAssetName(int evaluationID, String assetName) {
        String assetType = "";

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ASSET_TYPE)) {

            preparedStatement.setInt(1, evaluationID);
            preparedStatement.setString(2, assetName);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                assetType = resultSet.getString("AssetType");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return assetType;
    }
}
