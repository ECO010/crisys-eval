package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelatedAttackDAO {
    private static final String INSERT_ATTACK_RELATIONSHIPS = "INSERT INTO RelatedAttack (RelationFromCapecID, RelationToCapecID, Nature) VALUES (?, ?, ?)";
    private static final String FETCH_RELATED_ATTACKS = "SELECT RelationFromCapecID, Nature, RelationToCapecID\n" +
            "FROM RelatedAttack\n" +
            "WHERE Nature IN ('CanPrecede', 'ChildOf')\n" +
            "AND RelationToCapecID = ?";

    public void saveRelatedAttacks (List<RelatedAttack> relatedAttacks) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ATTACK_RELATIONSHIPS)) {

            for (RelatedAttack relatedAttack : relatedAttacks) {
                preparedStatement.setInt(1, relatedAttack.getRelationFromCapecId());
                preparedStatement.setInt(2, relatedAttack.getRelationToCapecId());
                preparedStatement.setString(3, relatedAttack.getNature());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    /**
     * TO-DO: Probably need to have an alert of some sort for attacks that don't have likelihoods, mitre attack mappings, mitigations, etc.
     * @param rootCapecId
     * @return
     */
    public List<AttackPattern> fetchRelatedAttacks(int rootCapecId) {

        List<AttackPattern> relatedAttacks = new ArrayList<>();
        AttackPatternDAO attackPatternDAO = new AttackPatternDAO();
        MitigationDAO mitigationDAO = new MitigationDAO();
        TaxonomyMappingDAO taxonomyMappingDAO = new TaxonomyMappingDAO();

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(FETCH_RELATED_ATTACKS)) {

            preparedStatement.setInt(1, rootCapecId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                int childCapecId = resultSet.getInt("RelationFromCapecId");

                // Create an Attack object and add it to the list
                AttackPattern relatedAttack = new AttackPattern();

                // Get information to display about the related attack
                relatedAttack.setCapecId(childCapecId);
                relatedAttack.setName(attackPatternDAO.getAttackNameFromDB(childCapecId));
                relatedAttack.setLikelihood(attackPatternDAO.getAttackLikelihoodFromDB(childCapecId));
                relatedAttack.setSeverity(attackPatternDAO.getAttackSeverityFromDB(childCapecId));
                relatedAttack.setDescription(attackPatternDAO.getAttackDescriptionFromDB(childCapecId));
                relatedAttack.setMitigations(mitigationDAO.getMitigationsForAttack(childCapecId));
                relatedAttack.setTaxonomyMappings(taxonomyMappingDAO.getTaxonomyMappingsForAttack(childCapecId));

                relatedAttacks.add(relatedAttack);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return relatedAttacks;
    }
}
