package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CommonWeaknessEnumerationDAO {
    private static final String INSERT_RELATED_WEAKNESSES = "INSERT OR IGNORE INTO RelatedWeakness (cweId, capecId) VALUES (?, ?)";
    private static final String INSERT_WEAKNESSES = "INSERT OR IGNORE INTO CommonWeaknessEnumeration (cweId, name, description, likelihoodOfExploit) VALUES (?, ?, ?, ?)";
    private static final String GET_WEAKNESS_DESCRIPTION = "SELECT Description FROM CommonWeaknessEnumeration WHERE CweId = ?";
    private static final String GET_WEAKNESS_LIKELIHOOD = "SELECT LikelihoodOfExploit FROM CommonWeaknessEnumeration WHERE CweId = ?";

    // Weird concatenation syntax because of SQLite
    private static final String GET_LINKED_CVEs =  "SELECT CVENumber\n" +
                                                    "FROM ICSAssetVulnerability\n" +
                                                    "WHERE ', ' || CWENumber || ',' LIKE '%' || ? || '%'\n" +
                                                    "AND AssetType = ?\n" +
                                                    "AND Year BETWEEN ? AND ?";

    public void saveRelatedWeaknesses(List<CommonWeaknessEnumeration> relatedWeaknesses) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_RELATED_WEAKNESSES)) {

            for (CommonWeaknessEnumeration relatedWeakness : relatedWeaknesses) {
                preparedStatement.setString(1, relatedWeakness.getCweId());
                preparedStatement.setInt(2, relatedWeakness.getCapecId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public void saveWeaknesses(List<CommonWeaknessEnumeration> weaknessEnumerationList) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_WEAKNESSES)) {

            for (CommonWeaknessEnumeration weaknessEnumeration : weaknessEnumerationList) {
                preparedStatement.setString(1, weaknessEnumeration.getCweId());
                preparedStatement.setString(2, weaknessEnumeration.getName());
                preparedStatement.setString(3, weaknessEnumeration.getDescription());
                preparedStatement.setString(4, weaknessEnumeration.getLikelihoodOfExploit());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public String getWeaknessDescriptionFromDB(String cweId) {
        String weaknessDescription = "";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_WEAKNESS_DESCRIPTION)) {

            preparedStatement.setString(1, cweId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                weaknessDescription = resultSet.getString("Description");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return weaknessDescription;
    }

    public String getWeaknessLikelihoodFromDB(String cweId) {
        String weaknessLikelihood = "";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_WEAKNESS_LIKELIHOOD)) {

            preparedStatement.setString(1, cweId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                weaknessLikelihood = resultSet.getString("LikelihoodOfExploit");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return weaknessLikelihood;
    }

    public String getLinkedCVEs(String cweId, String assetType, int yearFrom, int yearTo) {
        StringBuilder queryResult = new StringBuilder();
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_LINKED_CVEs)) {

            preparedStatement.setString(1, cweId);
            preparedStatement.setString(2, assetType);
            preparedStatement.setInt(3, yearFrom);
            preparedStatement.setInt(4, yearTo);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                // Append each value to the result string
                String linkedCVEs = resultSet.getString("CVENumber");
                queryResult.append(linkedCVEs).append(", ");
            }
            // Remove the last ", " from the result string
            if (queryResult.length() > 0) {
                queryResult.setLength(queryResult.length() - 2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return queryResult.toString();
    }
}
