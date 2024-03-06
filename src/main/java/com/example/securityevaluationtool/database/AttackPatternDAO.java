package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttackPatternDAO {
    //private static final String INSERT_ATTACK_PATTERN = "INSERT INTO AttackPattern (capecID, name, likelihood, severity, description) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_ATTACK_PATTERN = "UPDATE AttackPattern SET likelihood = ?, severity = ?, description = ? WHERE capecID = ?";
    private static final String GET_ATTACK_NAME = "Select Name from AttackPattern WHERE CapecId = ?";
    private static final String GET_ATTACK_SEVERITY = "Select Severity from AttackPattern WHERE CapecId = ?";
    private static final String GET_ATTACK_LIKELIHOOD = "Select Likelihood from AttackPattern WHERE CapecId = ?";
    private static final String GET_ATTACK_DESCRIPTION = "Select Description from AttackPattern WHERE CapecId = ?";

    public void saveAttackPatterns(List<AttackPattern> attackPatterns) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ATTACK_PATTERN)) {

            for (AttackPattern pattern : attackPatterns) {
                preparedStatement.setInt(4, pattern.getCapecId());
                //preparedStatement.setString(2, pattern.getName());
                preparedStatement.setString(1, pattern.getLikelihood());
                preparedStatement.setString(2, pattern.getSeverity());
                preparedStatement.setString(3, pattern.getDescription());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public List<Integer> getAllIcsCapecIds() {
        List<Integer> capecIDs = new ArrayList<>();

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT capecid FROM AttackPattern WHERE IsICSRelated = 1")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                int capecID = resultSet.getInt("capecid");
                capecIDs.add(capecID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return capecIDs;
    }

    public String getAttackNameFromDB(int capecId) {
        String attackName = "";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_NAME)) {

            preparedStatement.setInt(1, capecId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                attackName = resultSet.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return attackName;
    }

    public String getAttackLikelihoodFromDB(int capecId) {
        String attackLikelihood = "";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_LIKELIHOOD)) {

            preparedStatement.setInt(1, capecId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                attackLikelihood = resultSet.getString("Likelihood");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return attackLikelihood;
    }

    public String getAttackSeverityFromDB(int capecId) {
        String attackSeverity = "";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_SEVERITY)) {

            preparedStatement.setInt(1, capecId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                attackSeverity = resultSet.getString("Severity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return attackSeverity;
    }

    public String getAttackDescriptionFromDB(int capecId) {
        String attackDescription = "";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_DESCRIPTION)) {

            preparedStatement.setInt(1, capecId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                attackDescription = resultSet.getString("Description");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return attackDescription;
    }
}
