package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MitreAttackMitigationDAO {
    private static final String INSERT_ATTACK_MITIGATION = "INSERT INTO EnterpriseMitreAttackMitigation (EnterpriseMitigationName, EnterpriseTechniqueId, EnterpriseMitigationDescription) VALUES (?, ?, ?)";
    private static final String UPDATE_ATTACK_MITIGATION = "UPDATE EnterpriseMitreAttackMitigation SET ObjectId = ? WHERE EnterpriseMitigationId = ?";
    private static final String UPDATE_ICS_ATTACK_MITIGATION = "UPDATE ICSMitreAttackMitigation SET ObjectId = ? WHERE ICSMitigationId = ?";

    public void saveAttackMitigations(List<MitreAttackMitigation> mitreAttackMitigations) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ATTACK_MITIGATION)) {

            for (MitreAttackMitigation mitreAttackMitigation : mitreAttackMitigations) {
                preparedStatement.setString(1, mitreAttackMitigation.getMitigationName());
                preparedStatement.setString(2, mitreAttackMitigation.getMitigationId());
                preparedStatement.setString(3, mitreAttackMitigation.getMitigationDescription());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public void updateAttackTechniques(List<MitreAttackMitigation> mitreAttackMitigations) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ICS_ATTACK_MITIGATION)) {

            for (MitreAttackMitigation mitreAttackMitigation : mitreAttackMitigations) {
                preparedStatement.setString(1, mitreAttackMitigation.getObjectId());
                preparedStatement.setString(2, mitreAttackMitigation.getMitigationId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
