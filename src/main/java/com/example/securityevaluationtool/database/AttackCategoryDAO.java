package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AttackCategoryDAO {
    private static final String INSERT_ATTACK_CATEGORY = "INSERT INTO AttackCategory (CategoryID, CategoryName, CategorySummary) VALUES (?, ?, ?)";
    private static final String INSERT_CATEGORY_CAPEC_RELATIONSHIP = "INSERT INTO CapecIDCategoryRelationship (CategoryID, CapecID) VALUES (?, ?)";

    public void saveAttackCategories(List<AttackCategory> attackCategories) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ATTACK_CATEGORY)) {

            for (AttackCategory attackCategory : attackCategories) {
                preparedStatement.setInt(1, attackCategory.getCategoryId());
                preparedStatement.setString(2, attackCategory.getCategoryName());
                preparedStatement.setString(3, attackCategory.getCategorySummary());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public void saveCAPECAttackCategoriesRelationships(AttackCategory attackCategory) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CATEGORY_CAPEC_RELATIONSHIP)) {

            for (int capecId : attackCategory.getCapecIds()) {
                preparedStatement.setInt(1, attackCategory.getCategoryId());
                preparedStatement.setInt(2, capecId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
