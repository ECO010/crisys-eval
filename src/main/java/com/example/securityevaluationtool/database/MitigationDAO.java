package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MitigationDAO {
    private static final String INSERT_MITIGATIONS = "INSERT INTO Mitigation (mitigationDescription, capecId) VALUES (?, ?)";
    private static final String GET_MITIGATIONS_FOR_ATTACK = "SELECT mitigationDescription FROM Mitigation WHERE CapecId = ?";

    public void saveMitigations (List<Mitigation> mitigations) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MITIGATIONS)) {

            for (Mitigation mitigation : mitigations) {
                preparedStatement.setString(1, mitigation.getMitigationDescription());
                preparedStatement.setInt(2, mitigation.getCapecId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public List<Mitigation> getMitigationsForAttack(int capecId) {
        List<Mitigation> mitigations = new ArrayList<>();

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MITIGATIONS_FOR_ATTACK)) {

            preparedStatement.setInt(1, capecId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                String mitigationDescription = resultSet.getString("mitigationDescription");

                // Create an object and add it to the list
                Mitigation mitigation = new Mitigation();
                mitigation.setCapecId(capecId);
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
