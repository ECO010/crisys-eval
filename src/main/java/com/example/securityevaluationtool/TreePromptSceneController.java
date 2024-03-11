package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreePromptSceneController {

    private static final String GET_ATTACK_IMPACTS = "SELECT DISTINCT Impact FROM AttackImpact";
    private static final String GET_ATTACK_SCOPES = "SELECT DISTINCT Scope FROM AttackScope";
    public final String SCENE_TITLE = "Tree Prompt Scene";

    @FXML
    private VBox attackImpactBox;

    @FXML
    private VBox attackScopeBox;

    List<CheckBox> impactCheckBoxList = new ArrayList<>();
    List<CheckBox> scopeCheckBoxList = new ArrayList<>();

    private LandingSceneController previousController;

    public void setPreviousController(LandingSceneController previousController) {
        this.previousController = previousController;
    }

    /**
     * Populate Impact checkboxes on button click
     */
    @FXML
    private void onAttackImpactClick() {

        attackImpactBox.getChildren().clear();
        for (String option : fetchImpactDataFromDb()) {
            CheckBox checkBox = new CheckBox(option);
            attackImpactBox.getChildren().add(checkBox);
            impactCheckBoxList.add(checkBox);
        }
        attackImpactBox.setVisible(true);
    }

    /**
     * Populate Scope checkboxes on button click
     */
    @FXML
    private void onAttackScopeClick() {

        attackScopeBox.getChildren().clear();
        for (String option : fetchScopeDataFromDb()) {
            CheckBox checkBox = new CheckBox(option);
            attackScopeBox.getChildren().add(checkBox);
            scopeCheckBoxList.add(checkBox);
        }
        attackScopeBox.setVisible(true);
    }

    /**
     * The main logic which gets the list of capec id's which would be used to generate attack trees
     */
    private void generateAttackTree(int rootCapecId, TreeItem<AttackPattern> parentNode) {
        RelatedAttackDAO relatedAttackDAO = new RelatedAttackDAO();

        List<AttackPattern> relatedAttacks = relatedAttackDAO.fetchRelatedAttacks(rootCapecId);
        System.out.println(relatedAttacks.size());

        // For each related attack, create a TreeItem and add it as a child of the parent node
        for (AttackPattern relatedAttack : relatedAttacks) {
            TreeItem<AttackPattern> childNode = new TreeItem<>(relatedAttack);
            parentNode.getChildren().add(childNode);

            // Print the contents of the current node for testing
            System.out.println("Node CapecId: " + relatedAttack.getCapecId());
            System.out.println("Node Attack Pattern Name: " + relatedAttack.getName());

            // Recursively construct the tree for the child attack
            generateAttackTree(relatedAttack.getCapecId(), childNode);
        }
    }

    /**
     * Display the tree on a new scene as a TreeView
     */
    @FXML
    private void onNextClick(ActionEvent event) {

        // Random CapecId should be selected from the list of CapecId's
        int randomCapecId = 0;
        List <Integer> capecIds = new ArrayList<>();

        // Construct the SQL query based on the selected checkboxes
        StringBuilder impactQueryBuilder = new StringBuilder("SELECT DISTINCT AttackPattern.CapecID\n" +
                "FROM AttackPattern\n" +
                "INNER JOIN AttackImpact ON AttackPattern.CapecID = AttackImpact.CapecID\n" +
                "WHERE AttackPattern.IsICSRelated = 1\n" +
                "AND AttackImpact.Impact IN ");

        StringBuilder scopeQueryBuilder = new StringBuilder("SELECT DISTINCT AttackPattern.CapecID\n" +
                "FROM AttackPattern\n" +
                "INNER JOIN AttackScope ON AttackPattern.CapecID = AttackScope.CapecID\n" +
                "WHERE AttackPattern.IsICSRelated = 1\n" +
                "AND AttackScope.Scope IN ");

        List<String> impactSelectedOptions = new ArrayList<>();
        List<String> scopeSelectedOptions = new ArrayList<>();

        boolean isAnyScopeSelected = false;
        boolean isAnyImpactSelected = false;

        // Handle Scope Selection
        // Check if any scope checkbox is selected
        for (CheckBox scopeCheckBox : scopeCheckBoxList) {
            if (scopeCheckBox.isSelected()) {
                isAnyScopeSelected = true;
                scopeSelectedOptions.add("'" + scopeCheckBox.getText() + "'");
            }
        }

        // Handle Impact Selection
        // Check if any impact checkbox is selected
        for (CheckBox impactCheckBox : impactCheckBoxList) {
            if (impactCheckBox.isSelected()) {
                isAnyImpactSelected = true;
                impactSelectedOptions.add("'" + impactCheckBox.getText() + "'");
            }
        }

        // Check only Scope is Selected
        if (isAnyScopeSelected && !isAnyImpactSelected) {
            // If there's only one selected option
            if (scopeSelectedOptions.size() == 1) {
                scopeQueryBuilder.append("(").append(scopeSelectedOptions.get(0)).append(")");
            } else {
                // If there are multiple selected options
                scopeQueryBuilder.append("(")
                        .append(String.join(", ", scopeSelectedOptions))
                        .append(")");
            }

            // Testing the right query is being used
            System.out.println(scopeQueryBuilder);

            // Execute the SQL query and process the results
            try (Connection connection = DatabaseConnector.connect();
                 PreparedStatement statement = connection.prepareStatement(scopeQueryBuilder.toString());
                 ResultSet resultSet = statement.executeQuery()) {

                // Process the resultSet
                while (resultSet.next()) {
                    // Retrieve data from the resultSet and add it to a list of id's
                    int capecId = resultSet.getInt("CapecID");
                    capecIds.add(capecId);
                }
                // select random id
                randomCapecId = capecIds.get(new Random().nextInt(capecIds.size()));

                // Testing selected id
                System.out.println(randomCapecId);

            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exceptions
            }
        }

        // Check only Impact is Selected
        if (isAnyImpactSelected && !isAnyScopeSelected) {
            // If there's only one selected option
            if (impactSelectedOptions.size() == 1) {
                impactQueryBuilder.append("(").append(impactSelectedOptions.get(0)).append(")");
            } else {
                // If there are multiple selected options
                impactQueryBuilder.append("(")
                        .append(String.join(", ", impactSelectedOptions))
                        .append(")");
            }

            // testing the query being used
            System.out.println(impactQueryBuilder);

            // Execute the SQL query and process the results
            try (Connection connection = DatabaseConnector.connect();
                 PreparedStatement statement = connection.prepareStatement(impactQueryBuilder.toString());
                 ResultSet resultSet = statement.executeQuery()) {

                // Process the resultSet
                while (resultSet.next()) {
                    // Retrieve data from the resultSet and add it to a list of capec id's
                    int capecId = resultSet.getInt("CapecID");
                    capecIds.add(capecId);
                }

                // select random capec id
                randomCapecId = capecIds.get(new Random().nextInt(capecIds.size()));

                // Testing selected id
                System.out.println(randomCapecId);
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exceptions
            }
        }

        // Both are selected, error and make sure the right input is gotten before generating the tree.
        if (isAnyImpactSelected && isAnyScopeSelected) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Selection Error");
            alert.setContentText("Please select either Impact or Scope, not both.");
            alert.showAndWait();
            return; //Prevent navigation
        }

        // No filter selected, use a random ID from all ICS Attack patterns
        if (!isAnyScopeSelected && !isAnyImpactSelected) {
            AttackPatternDAO attackPatternDAO = new AttackPatternDAO();
            capecIds = attackPatternDAO.getAllIcsCapecIds();
            randomCapecId = capecIds.get(new Random().nextInt(capecIds.size()));

            // Testing selected id
            System.out.println(randomCapecId);
        }

        // Set RandomId fetched as root
        int rootCapecId = randomCapecId;

        // Create instances of the DAOs needed
        AttackPatternDAO attackPatternDAO = new AttackPatternDAO();
        TaxonomyMappingDAO taxonomyMappingDAO = new TaxonomyMappingDAO();
        MitigationDAO mitigationDAO = new MitigationDAO();

        // Create a TreeItem for the root node
        AttackPattern rootAttack = new AttackPattern();

        // set root attack details
        rootAttack.setCapecId(rootCapecId);
        rootAttack.setName(attackPatternDAO.getAttackNameFromDB(rootCapecId));
        rootAttack.setSeverity(attackPatternDAO.getAttackSeverityFromDB(rootCapecId));
        rootAttack.setLikelihood(attackPatternDAO.getAttackLikelihoodFromDB(rootCapecId));
        rootAttack.setDescription(attackPatternDAO.getAttackDescriptionFromDB(rootCapecId).trim()); // trimming description because it shows up weird in the DB

        List<TaxonomyMapping> taxonomyMappings = taxonomyMappingDAO.getTaxonomyMappingsForAttack(rootCapecId);
        rootAttack.setTaxonomyMappings(taxonomyMappings);

        List<Mitigation> mitigations = mitigationDAO.getMitigationsForAttack(rootCapecId);
        rootAttack.setMitigations(mitigations);

        //TreeItem<AttackPattern> rootNode = new TreeItem<>(rootAttack);
        TreeItem<AttackPattern> rootNode = CustomTreeItemFactory.createTreeItem(rootAttack);

        // Generate the attack tree recursively starting from the root node
        generateAttackTree(rootCapecId, rootNode);

        // Navigate to the Tree View Scene
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tree-view-scene.fxml"));
            Parent root = loader.load();

            /*TreePromptSceneController controller = loader.getController();
            controller.setPreviousController(this);*/

            // Get the controller of the tree view scene
            TreeViewSceneController treeViewSceneController = loader.getController();

            // Pass the root node of the tree to the controller
            treeViewSceneController.setRootNode(rootNode);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(treeViewSceneController.SCENE_TITLE);
            stage.show();

            // Close the current scene if needed
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close(); // Close instead of hide
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
    }

    @FXML
    private void onBackClick(ActionEvent event) {
        try {
            // Load the FXML file of the landing scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("landing-scene.fxml"));
            Parent root = loader.load();

            // Get the controller of the landing scene
            LandingSceneController landingSceneController = loader.getController();

            // Set the landing scene controller as the previous controller
            // This allows for communication between scenes if needed
            // landingSceneController.setPreviousController(this);

            // Get the current stage from the event source
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene of the current stage to the landing scene
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(landingSceneController.SCENE_TITLE);

            // Show the stage if it's not already showing
            if (!currentStage.isShowing()) {
                currentStage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
    }


    /**
     * Fetch Impact Options from the DB
     * @return impactOptions
     */
    private List<String> fetchImpactDataFromDb() {
        List<String> impactOptions = new ArrayList<>();

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_IMPACTS);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iterate over the result set and populate the impactOptions list
            while (resultSet.next()) {
                String impact = resultSet.getString("Impact");
                impactOptions.add(impact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return impactOptions;
    }

    /**
     * Fetch Scope Options from the DB
     * @return scopeOptions
     */
    private List<String> fetchScopeDataFromDb() {
        List<String> scopeOptions = new ArrayList<>();

        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ATTACK_SCOPES);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iterate over the result set and populate the scopeOptions list
            while (resultSet.next()) {
                String scope = resultSet.getString("Scope");
                scopeOptions.add(scope);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return scopeOptions;
    }
}
