package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ExampleInstanceDAO {
    private static final String INSERT_EXAMPLE_INSTANCE = "INSERT INTO ExampleInstance (exampleInstanceDescription, capecId) VALUES (?, ?)";

    public void saveExampleInstances(List<ExampleInstance> exampleInstances) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EXAMPLE_INSTANCE)) {

            for (ExampleInstance exampleInstance : exampleInstances) {
                preparedStatement.setString(1, exampleInstance.getExampleInstanceDescription());
                preparedStatement.setInt(2, exampleInstance.getCapecId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
