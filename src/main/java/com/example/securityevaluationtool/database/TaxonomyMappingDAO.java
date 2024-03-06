package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaxonomyMappingDAO {
    private static final String INSERT_MAPPINGS = "INSERT INTO TaxonomyMappings (AttackTechniqueName, AttackTechniqueId, capecId) VALUES (?, ?, ?)";
    private static final String GET_TAXONOMY_MAPPINGS = "Select AttackTechniqueId from TaxonomyMappings WHERE CapecId = ?";

    public void saveMappings (List<TaxonomyMapping> mappings) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MAPPINGS)) {

            for (TaxonomyMapping mapping : mappings) {
                preparedStatement.setString(1, mapping.getAttackTechniqueName());
                preparedStatement.setString(2, mapping.getAttackTechniqueId());
                preparedStatement.setInt(3, mapping.getCapecId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public List<TaxonomyMapping> getTaxonomyMappingsForAttack(int capecId) {
        List<TaxonomyMapping> taxonomyMappings = new ArrayList<>();

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TAXONOMY_MAPPINGS)) {

            preparedStatement.setInt(1, capecId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                String attackTechniqueId = resultSet.getString("attackTechniqueId");

                // Create an object and add it to the list
                TaxonomyMapping taxonomyMapping = new TaxonomyMapping();
                taxonomyMapping.setCapecId(capecId);
                taxonomyMapping.setAttackTechniqueId(attackTechniqueId);
                taxonomyMappings.add(taxonomyMapping);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return taxonomyMappings;
    }
}
