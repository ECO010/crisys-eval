package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CveDAO {
    private static final String INSERT_CVEs = "INSERT OR IGNORE INTO CommonVulnerabilityExposure (cveNum, cweId, title, description) VALUES (?, ?, ?, ?)";

    public void saveCVEs(List<Cve> cvesToSave) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CVEs)) {

            for (Cve cveToSave : cvesToSave) {
                preparedStatement.setString(1, cveToSave.getCveNum());
                preparedStatement.setString(2, cveToSave.getCweId());
                preparedStatement.setString(3, cveToSave.getCveTitle());
                preparedStatement.setString(4, cveToSave.getDescription());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
