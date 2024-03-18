package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CommonWeaknessEnumerationDAO {
    private static final String INSERT_RELATED_WEAKNESSES = "INSERT OR IGNORE INTO RelatedWeakness (cweId, capecId) VALUES (?, ?)";

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
}
