package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluationDAO {

    private static final String ADD_EVALUATION = "INSERT OR IGNORE INTO Evaluation (SystemName, EvalDT) VALUES (?, ?)";
    private static final String ADD_EVALUATION_ASSET = "INSERT OR IGNORE INTO EvaluationAsset (EvaluationID, AssetName, AssetType) VALUES (?, ?, ?)";
    private static final String GET_LATEST_EVAL_ID = "SELECT EvaluationID FROM Evaluation ORDER BY EvaluationID DESC LIMIT 1";
    private static final String GET_SYSTEM_SAFETY_SCORE = "SELECT EvalScore FROM Evaluation WHERE EvaluationID = ?";
    private static final String GET_EVALUATIONS = "SELECT * FROM Evaluation";
    private static final String GET_ASSET_SAFETY_SCORE = "SELECT AssetSafetyScore FROM EvaluationAsset WHERE EvaluationID = ? AND AssetName = ?";
    private static final String GET_ASSET_TYPE = "SELECT AssetType FROM EvaluationAsset WHERE EvaluationID = ? AND AssetName = ?";
    private static final String GET_EVAL_ASSET_DATA = "SELECT EvaluationID, AssetName, AssetType, AssetSafetyScore FROM EvaluationAsset WHERE EvaluationID = ?";
    private static final String GET_EVAL_DATA = "SELECT * FROM Evaluation WHERE EvaluationID = ?";
    private static final String GET_ATTACK_TREE_DATA = "SELECT * FROM AttackTreeData WHERE EvaluationID = ?";
    private static final String GET_ATTACK_TREE_YEAR_FROM = "SELECT YearFrom FROM AttackTreeData WHERE EvaluationID = ?";
    private static final String GET_ATTACK_TREE_YEAR_TO = "SELECT YearTo FROM AttackTreeData WHERE EvaluationID = ?";
    private static final String UPDATE_ASSET_SCORE = "UPDATE EvaluationAsset SET AssetSafetyScore = ? WHERE AssetName = ? AND EvaluationID = ?";
    private static final String UPDATE_SYSTEM_SCORE = "UPDATE Evaluation SET EvalScore = ? WHERE EvaluationID = ?";
    private static final String ADD_ATTACK_TREE_DATA = "INSERT OR IGNORE INTO AttackTreeData (EvaluationID, Root, YearFrom, YearTo) VALUES (?, ?, ?, ?)";


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

    public double getSystemSafetyScore(int evaluationID) {
        double systemSafetyScore = 0.0;

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SYSTEM_SAFETY_SCORE)) {

            preparedStatement.setInt(1, evaluationID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                systemSafetyScore = resultSet.getInt("EvalScore");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return systemSafetyScore;
    }

    public int getAssetSafetyScore(int evaluationID, String assetName) {
        int assetSafetyScore = 0;

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ASSET_SAFETY_SCORE)) {

            preparedStatement.setInt(1, evaluationID);
            preparedStatement.setString(2, assetName);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                assetSafetyScore = resultSet.getInt("AssetSafetyScore");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return assetSafetyScore;
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

    public void updateAssetScore(int currentAssetSafetyScore, String currentAssetName, int evaluationId) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ASSET_SCORE)) {
            preparedStatement.setInt(1, currentAssetSafetyScore);
            preparedStatement.setString(2, currentAssetName);
            preparedStatement.setInt(3, evaluationId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public void updateSystemSafetyScore(int evaluationId) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SYSTEM_SCORE)) {

            // Subquery to calculate the average of AssetSafetyScore for the given EvaluationID
            String subquery = "SELECT AVG(AssetSafetyScore) AS AvgScore FROM EvaluationAsset WHERE EvaluationID = ?";
            PreparedStatement subStatement = connection.prepareStatement(subquery);
            subStatement.setInt(1, evaluationId);
            ResultSet resultSet = subStatement.executeQuery();

            // Retrieve the average score from the result set
            double avgScore = 0.0;
            if (resultSet.next()) {
                avgScore = resultSet.getDouble("AvgScore");
            }

            // Set the average score as the system safety score in the UPDATE statement
            preparedStatement.setDouble(1, avgScore);
            preparedStatement.setInt(2, evaluationId);

            // Execute the update statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public List<Evaluation> getEvaluationsFromDatabase() {
        List<Evaluation> evaluations = new ArrayList<>();

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EVALUATIONS)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Evaluation evaluation = new Evaluation();

                evaluation.setEvaluationDate(resultSet.getString("evalDT"));
                evaluation.setCriticalSystemName(resultSet.getString("systemName"));
                evaluation.setEvaluationScore(resultSet.getDouble("EvalScore"));
                evaluation.setEvaluationID(resultSet.getInt("EvaluationID"));

                evaluations.add(evaluation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return evaluations;
    }

    public void deleteEvaluationAssets(List<Integer> evaluationIDs) {
        String sql = "DELETE FROM EvaluationAsset WHERE EvaluationID IN (" +
                evaluationIDs.stream().map(Object::toString).collect(Collectors.joining(",")) +
                ")";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public void deleteEvaluation(List<Integer> evaluationIDs) {
        String sql = "DELETE FROM Evaluation WHERE EvaluationID IN (" +
                evaluationIDs.stream().map(Object::toString).collect(Collectors.joining(",")) +
                ")";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public void deleteAttackTreeData(List<Integer> evaluationIDs) {
        String sql = "DELETE FROM AttackTreeData WHERE EvaluationID IN (" +
                evaluationIDs.stream().map(Object::toString).collect(Collectors.joining(",")) +
                ")";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public Evaluation retrieveEvaluationData(int evaluationID) {
        Evaluation evaluation = new Evaluation();
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EVAL_DATA)) {

            preparedStatement.setInt(1, evaluationID);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Fetch evaluation details
            while (resultSet.next()) {
                evaluation.setCriticalSystemName(resultSet.getString("SystemName"));
                evaluation.setEvaluationDate(resultSet.getString("EvalDT"));
                evaluation.setEvaluationID(resultSet.getInt("EvaluationID"));
                evaluation.setEvaluationScore(resultSet.getDouble("EvalScore"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return evaluation;
    }

    public List<EvaluationAsset> retrieveEvaluationAssetData(int evaluationID) {
        // Fetch evaluation assets
        List<EvaluationAsset> evaluationAssets = new ArrayList<>();
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_EVAL_ASSET_DATA)) {

            preparedStatement.setInt(1, evaluationID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                EvaluationAsset evaluationAsset = new EvaluationAsset();
                evaluationAsset.setAssetType(resultSet.getString("AssetType"));
                evaluationAsset.setAssetName(resultSet.getString("AssetName"));
                evaluationAsset.setEvaluationID(resultSet.getInt("EvaluationID"));
                evaluationAsset.setAssetSafetyScore(resultSet.getInt("AssetSafetyScore"));
                evaluationAssets.add(evaluationAsset);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return evaluationAssets;
    }

    public void saveAttackTreeData(int evaluationID, String root, int yearFrom, int yearTo) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_ATTACK_TREE_DATA)) {
            preparedStatement.setInt(1, evaluationID);
            preparedStatement.setString(2, root);
            preparedStatement.setInt(3, yearFrom);
            preparedStatement.setInt(4, yearTo);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public int getTreeYearFrom(int evaluationID) {
        int yearFrom = 0;
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_TREE_YEAR_FROM)) {

            preparedStatement.setInt(1, evaluationID);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Fetch evaluation details
            while (resultSet.next()) {
                yearFrom = resultSet.getInt("YearFrom");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return yearFrom;
    }

    public int getTreeYearTo(int evaluationID) {
        int yearTo = 0;
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_TREE_YEAR_TO)) {

            preparedStatement.setInt(1, evaluationID);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Fetch evaluation details
            while (resultSet.next()) {
                yearTo = resultSet.getInt("YearTo");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return yearTo;
    }

    public AttackTreeData retrieveAttackTreeData(int evaluationID) {
        AttackTreeData attackTreeData = new AttackTreeData();
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_TREE_DATA)) {

            preparedStatement.setInt(1, evaluationID);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Fetch attack tree details
            while (resultSet.next()) {
                attackTreeData.setEvaluationID(resultSet.getInt("EvaluationID"));
                attackTreeData.setRoot(resultSet.getString("Root"));
                attackTreeData.setYearFrom(resultSet.getInt("YearFrom"));
                attackTreeData.setYearTo(resultSet.getInt("YearTo"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return attackTreeData;
    }
}
