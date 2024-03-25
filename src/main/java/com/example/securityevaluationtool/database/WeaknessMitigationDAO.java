package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WeaknessMitigationDAO {
    private static final String INSERT_MITIGATIONS = "INSERT INTO WeaknessMitigation (mitigationDescription, cweId) VALUES (?, ?)";
    private static final String GET_MITIGATIONS_FOR_WEAKNESS = "SELECT mitigationDescription FROM WeaknessMitigation WHERE CweId = ?";

    public void saveMitigations (List<WeaknessMitigation> mitigations) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MITIGATIONS)) {

            for (WeaknessMitigation mitigation : mitigations) {
                preparedStatement.setString(1, mitigation.getMitigationDescription());
                preparedStatement.setString(2, mitigation.getCweId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public List<WeaknessMitigation> getMitigationsForWeakness(String cweId) {
        List<WeaknessMitigation> mitigations = new ArrayList<>();

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MITIGATIONS_FOR_WEAKNESS)) {

            preparedStatement.setString(1, cweId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                String mitigationDescription = resultSet.getString("mitigationDescription");

                // Create an object and add it to the list
                WeaknessMitigation mitigation = new WeaknessMitigation();
                mitigation.setCweId(cweId);
                mitigation.setMitigationDescription(mitigationDescription);
                mitigations.add(mitigation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return mitigations;
    }
}
