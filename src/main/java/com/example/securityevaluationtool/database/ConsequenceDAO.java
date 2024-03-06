package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ConsequenceDAO {

    public void saveAttackConsequences(List<Consequence> consequences) {
        try {
            Connection connection = DatabaseConnector.connect();

            // Loop through each consequence
            for (Consequence consequence : consequences) {

                // Get the capecid, the list of scopes and impacts
                int capecId = consequence.getCapecId();
                List<String> scopes = consequence.getScopes();
                List<String> impacts = consequence.getImpacts();

                // Insert each scope for the consequence
                for (String scope : scopes) {
                    String sql = "INSERT INTO attackScope (capecId, scope) VALUES (?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, capecId);
                    statement.setString(2, scope);
                    statement.executeUpdate();
                }

                // Insert each impact for the consequence
                for (String impact : impacts) {
                    String sql = "INSERT INTO attackImpact (capecId, impact) VALUES (?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, capecId);
                    statement.setString(2, impact);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }
}
