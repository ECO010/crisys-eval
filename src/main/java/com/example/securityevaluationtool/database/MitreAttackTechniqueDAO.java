package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MitreAttackTechniqueDAO {
    private static final String INSERT_ATTACK_TECHNIQUE = "INSERT INTO EnterpriseMitreAttackTechnique (EnterpriseTechniqueName, EnterpriseTechniqueDescription, EnterpriseTechniqueId) VALUES (?, ?, ?)";
    private static final String UPDATE_ATTACK_TECHNIQUE = "UPDATE EnterpriseMitreAttackTechnique SET ObjectId = ? WHERE EnterpriseTechniqueId = ?";
    private static final String UPDATE_ICS_ATTACK_TECHNIQUE = "UPDATE ICSMitreAttackTechnique SET ObjectId = ? WHERE ICSTechniqueId = ?";

    public void saveAttackTechniques(List<MitreAttackTechnique> mitreAttackTechniques) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ATTACK_TECHNIQUE)) {

            for (MitreAttackTechnique mitreAttackTechnique : mitreAttackTechniques) {
                preparedStatement.setString(1, mitreAttackTechnique.getAttackTechniqueName());
                preparedStatement.setString(2, mitreAttackTechnique.getAttackTechniqueDescription());
                preparedStatement.setString(3, mitreAttackTechnique.getAttackTechniqueId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public void updateAttackTechniques(List<MitreAttackTechnique> mitreAttackTechniques) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ICS_ATTACK_TECHNIQUE)) {

            for (MitreAttackTechnique mitreAttackTechnique : mitreAttackTechniques) {
                preparedStatement.setString(1, mitreAttackTechnique.getObjectId());
                preparedStatement.setString(2, mitreAttackTechnique.getAttackTechniqueId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
