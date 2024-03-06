package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class KillChainPhaseDAO {
    private static final String INSERT_KILL_CHAIN = "INSERT INTO EnterpriseTechniqueKillChain (EnterpriseTechniqueId, KillChainPhase) VALUES (?, ?)";

    public void saveKillChainPhases(List<KillChainPhase> killChainPhases) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_KILL_CHAIN)) {

            for (KillChainPhase killChainPhase : killChainPhases) {
                preparedStatement.setString(1, killChainPhase.getAttackTechniqueId());
                preparedStatement.setString(2, killChainPhase.getKillChainPhase());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
