package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ICSAssetDAO {
    private static final String INSERT_ICS_ASSET = "INSERT OR IGNORE INTO ICSAssetFromCisa (AssetType, AssetDescription) VALUES (?, ?)";

    public void saveIcsAssets(List<ICSAsset> icsAssets) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ICS_ASSET)) {

            for (ICSAsset icsAsset : icsAssets) {
                preparedStatement.setString(1, icsAsset.getAssetType());
                preparedStatement.setString(2, icsAsset.getDescription());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
