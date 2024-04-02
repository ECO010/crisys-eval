package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EvaluationListController {
    public final String SCENE_TITLE = "List Of Evaluations";
    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<Evaluation, String> evalDT;

    @FXML
    private TableColumn<Evaluation,Integer> evaluationID;

    @FXML
    private TableColumn<Evaluation, Double> evaluationScore;

    @FXML
    private TableView<Evaluation> evaluationTable;

    @FXML
    private Button loadButton;

    @FXML
    private TableColumn<Evaluation, String> systemName;

    private final EvaluationDAO evaluationDAO = new EvaluationDAO();

    private Evaluation currentEvaluation;
    private List<EvaluationAsset> retrievedEvaluationAssets;

    private int yearTo;
    private int yearFrom;
    private String assetType;
    private String assetName;

    // DAO Objects
    private final ICSAssetVulnerabilityDAO icsAssetVulnerabilityDAO = new ICSAssetVulnerabilityDAO();
    private final AttackPatternDAO attackPatternDAO = new AttackPatternDAO();

    @FXML
    private void initialize() {
        // Configure the columns
        // these should match up with the object properties/fields (so Evaluation in this case)
        systemName.setCellValueFactory(new PropertyValueFactory<>("criticalSystemName"));
        evalDT.setCellValueFactory(new PropertyValueFactory<>("evaluationDate"));
        evaluationScore.setCellValueFactory(new PropertyValueFactory<>("evaluationScore"));
        evaluationID.setCellValueFactory(new PropertyValueFactory<>("evaluationID"));

        // Load evaluations from the database
        List<Evaluation> evaluations = evaluationDAO.getEvaluationsFromDatabase();

        // Add evaluations to the TableView
        evaluationTable.getItems().addAll(evaluations);

        // Make the table multi-select
        evaluationTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    // Make sure a row is selected
    // Delete all assets linked to the selected evalID 1st, then delete the evaluation as a whole
    @FXML
    private void onDeleteClick() {
        // Get the selected evaluations from the TableView
        ObservableList<Evaluation> selectedEvaluations = evaluationTable.getSelectionModel().getSelectedItems();
        if (!selectedEvaluations.isEmpty()) {
            // Collect evaluation IDs
            List<Integer> evaluationIDs = selectedEvaluations.stream()
                    .map(Evaluation::getEvaluationID)
                    .collect(Collectors.toList());

            System.out.println(evaluationIDs);

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Deletion");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you would like to delete the selected evaluation(s)?");

            // Add OK and Cancel buttons to the confirmation dialog
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Show the confirmation dialog and wait for user input
            Optional<ButtonType> result = confirmation.showAndWait();

            // Check the user's choice, User confirmed
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Delete EvalAssets first
                evaluationDAO.deleteEvaluationAssets(evaluationIDs);

                // Delete Attack Tree Data
                evaluationDAO.deleteAttackTreeData(evaluationIDs);

                // Then delete Evaluations
                evaluationDAO.deleteEvaluation(evaluationIDs);

                // Remove selected evaluations from the TableView
                evaluationTable.getItems().removeAll(selectedEvaluations);
            }
        } else {
            // Show an error message indicating no evaluation is selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select evaluations to delete.");
            alert.showAndWait();
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

    // Not sure how I'm going to do this at the moment
    // I'm thinking retrieve stored instances of only the tree view and the evaluation end
    // User can then save the PDF or CSV (Excel result)
    @FXML
    private void onLoadClick() throws IOException {
        Evaluation selectedEvaluation = evaluationTable.getSelectionModel().getSelectedItem();
        // Get the selected evaluations from the TableView
        ObservableList<Evaluation> selectedEvaluations = evaluationTable.getSelectionModel().getSelectedItems();
        // Retrieve the EvaluationData instance for the selected evaluation
        currentEvaluation = evaluationDAO.retrieveEvaluationData(selectedEvaluation.getEvaluationID());
        currentEvaluation.setEvaluationID(selectedEvaluation.getEvaluationID());
        retrievedEvaluationAssets = evaluationDAO.retrieveEvaluationAssetData(selectedEvaluation.getEvaluationID());
        yearFrom = evaluationDAO.getTreeYearFrom(selectedEvaluation.getEvaluationID());
        yearTo = evaluationDAO.getTreeYearTo(selectedEvaluation.getEvaluationID());

        // Can only load one evaluation, error if more than one is selected
        if (selectedEvaluations.isEmpty()) {
            // Show an error message indicating no evaluation is selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select an evaluation to load.");
            alert.showAndWait();
        }
        else if (selectedEvaluations.size() > 1) {
            // Show an error message indicating no evaluation is selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Only one evaluation can be loaded at a time.");
            alert.showAndWait();
        }
        // load both the evaluation end and the tree view of the selected evaluation
        else {
            // Load the evaluation end scene
            FXMLLoader evalEndLoader = new FXMLLoader(getClass().getResource("evaluation-end.fxml"));
            Parent evalEndRoot = evalEndLoader.load();
            EvaluationEndController evalEndController = evalEndLoader.getController();
            evalEndController.getCurrentEvaluation(currentEvaluation);
            evalEndController.getSystemSafetyScore(currentEvaluation.getEvaluationScore());
            evalEndController.updateProgress(currentEvaluation.getEvaluationScore());
            evalEndController.updateHeading();

            // Load the tree view scene
            FXMLLoader treeViewLoader = new FXMLLoader(getClass().getResource("tree-view-scene.fxml"));
            Parent treeViewRoot = treeViewLoader.load();

            // Generate attack tree again using saved data from DB
            TreeItem<String> rootItem = generateAttackTree(yearFrom, yearTo);

            TreeViewSceneController treeViewController = treeViewLoader.getController();

            // don't attach context menu
            treeViewController.setRootNode(rootItem, false);

            // Create a new window for each scene and display them side by side
            Stage evalEndStage = new Stage();
            evalEndStage.setTitle(evalEndController.SCENE_TITLE);
            evalEndStage.setScene(new Scene(evalEndRoot));
            evalEndStage.show();

            Stage treeViewStage = new Stage();
            treeViewStage.setTitle(treeViewController.SCENE_TITLE);
            treeViewStage.setScene(new Scene(treeViewRoot));
            treeViewStage.show();
            treeViewStage.toBack();

            // Make continue button invisible on tree view scene, all they can do from here is save as PDF
            treeViewController.continueBtn.setVisible(false);
        }
    }

    // Ended up having to generate the tree again
    private TreeItem<String> generateAttackTree(int yearFrom, int yearTo) {
        // Root of the tree, the evaluated system name. Is hidden on the tree view
        TreeItem<String> rootItem = new TreeItem<>(currentEvaluation.getCriticalSystemName());

        // Go through evaluation assets passed from previous scene and get the asset type and the asset name
        for (EvaluationAsset evaluationAsset : retrievedEvaluationAssets) {
            assetType = evaluationAsset.getAssetType();
            assetName = evaluationAsset.getAssetName();

            // Set the asset name as the tree item and add it to the root
            TreeItem<String> assetItem = new TreeItem<>(assetName);
            rootItem.getChildren().add(assetItem);

            // Get distinct CWE's related to the asset type and users year selection
            List<String> fetchedCWEs = icsAssetVulnerabilityDAO.getDistinctCweIdsBasedOnYearAndAssetType(assetType, yearFrom, yearTo);

            // use distinct CWE strings to fetch list of CWE Objects
            List<CommonWeaknessEnumeration> assetWeaknesses = icsAssetVulnerabilityDAO.getCweFromStrings(fetchedCWEs);

            // Go through each weakness and add it as a child to the assetItem if there is a CWE linked
            if (!assetWeaknesses.isEmpty()) {

                // To string method displays CweId alone (could change it up later)
                for (CommonWeaknessEnumeration weaknessEnumeration: assetWeaknesses) {
                    TreeItem<String> cweItem = new TreeItem<>(weaknessEnumeration.toString());
                    assetItem.getChildren().add(cweItem);

                    // Get the list of CapecIds from the CWE Item
                    List<Integer> relatedCapecIds = attackPatternDAO.getCapecIdsFromCwe(weaknessEnumeration.getCweId());

                    // Get list of attack pattern objects from capec Ids
                    List<AttackPattern> relatedAttackPatterns = attackPatternDAO.getAttackPatternsFromIds(relatedCapecIds);

                    // Loop through list of related attack patterns and add each one as a child to the relevant cwe Item if it isn't empty
                    if (!relatedAttackPatterns.isEmpty()) {
                        for (AttackPattern attackPattern : relatedAttackPatterns) {
                            TreeItem<String> attackPatternItem = new TreeItem<>(attackPattern.toString());
                            cweItem.getChildren().add(attackPatternItem);
                        }
                    }
                }
            }
        }
        return rootItem;
    }
}
