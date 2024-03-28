package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class EpssRecordDAO {
    private static final String INSERT_EPSS = "INSERT OR IGNORE INTO EPSS (cveNumber, epss, percentile) VALUES (?, ?, ?)";

    public void saveEpssRecords(List<EpssRecord> epssRecordsToSave) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EPSS)) {

            for (EpssRecord epssRecordToSave : epssRecordsToSave) {
                preparedStatement.setString(1, epssRecordToSave.getCveNumber());
                preparedStatement.setDouble(2, epssRecordToSave.getEpssScore());
                preparedStatement.setDouble(3, epssRecordToSave.getPercentile());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
