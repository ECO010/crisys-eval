package com.example.securityevaluationtool.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttackStepDAO {
    private static final String INSERT_ATTACK_STEP = "INSERT INTO AttackStep (step, phase, attackStepDescription, capecId) VALUES (?, ?, ?, ?)";
    private static final String GET_ATTACK_STEPS = "Select Step, Phase, AttackStepDescription from AttackStep WHERE CapecId = ?";
    private static final String INSERT_ATTACK_STEP_TECHNIQUE = "INSERT INTO AttackStepTechnique (RelatedAttackStep, StepTechnique, CapecId) VALUES (?, ?, ?)";
    private static final String GET_ATTACK_STEP_TECHNIQUES = "Select StepTechnique from AttackStepTechnique WHERE CapecId = ? AND RelatedAttackStep = ?";

    public void saveAttackSteps(List<AttackStep> attackSteps) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ATTACK_STEP)) {

            for (AttackStep step : attackSteps) {
                preparedStatement.setString(1, step.getStep());
                preparedStatement.setString(2, step.getPhase());
                preparedStatement.setString(3, step.getAttackStepDescription());
                preparedStatement.setInt(4, step.getCapecId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public List<AttackStep> getAttackSteps(int capecId) {
        List<AttackStep> attackSteps = new ArrayList<>();
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_STEPS)) {

            preparedStatement.setInt(1, capecId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                AttackStep attackStep = new AttackStep();
                attackStep.setAttackStepDescription(resultSet.getString("AttackStepDescription"));
                attackStep.setStep(resultSet.getString("Step"));
                attackStep.setPhase(resultSet.getString("Phase"));
                attackStep.setCapecId(capecId);

                attackSteps.add(attackStep);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return attackSteps;
    }

    public void saveAttackStepTechniques(List<AttackStepTechnique> attackStepTechniques) {
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ATTACK_STEP_TECHNIQUE)) {

            for (AttackStepTechnique stepTechnique : attackStepTechniques) {
                preparedStatement.setString(1, stepTechnique.getAttackStep());
                preparedStatement.setString(2, stepTechnique.getTechnique());
                preparedStatement.setInt(3, stepTechnique.getCapecId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    public List<AttackStepTechnique> getAttackStepTechniques(int capecId, String step) {
        List<AttackStepTechnique> attackStepTechniques = new ArrayList<>();
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_STEP_TECHNIQUES)) {

            preparedStatement.setInt(1, capecId);
            preparedStatement.setString(2, step);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Retrieve the CAPEC IDs and add them to the list
            while (resultSet.next()) {
                AttackStepTechnique attackStepTechnique = new AttackStepTechnique();
                attackStepTechnique.setTechnique(resultSet.getString("StepTechnique"));
                attackStepTechnique.setAttackStep(step);
                attackStepTechnique.setCapecId(capecId);

                attackStepTechniques.add(attackStepTechnique);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return attackStepTechniques;
    }
}
