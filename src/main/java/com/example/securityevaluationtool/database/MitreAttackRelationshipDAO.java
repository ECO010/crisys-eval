package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MitreAttackRelationshipDAO {
    private static final String INSERT_ATTACK_RELATIONSHIP = "INSERT INTO EnterpriseMitigationAttackTechnique (MitigationObjectId, TechniqueObjectId, Action) VALUES (?, ?, ?)";


    public void saveAttackRelationships(List<MitreAttackRelationship> mitreAttackRelationships) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ATTACK_RELATIONSHIP)) {

            for (MitreAttackRelationship mitreAttackRelationship : mitreAttackRelationships) {
                preparedStatement.setString(1, mitreAttackRelationship.getMitigationObjectId());
                preparedStatement.setString(2, mitreAttackRelationship.getTechniqueObjectId());
                preparedStatement.setString(3, mitreAttackRelationship.getAction());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
