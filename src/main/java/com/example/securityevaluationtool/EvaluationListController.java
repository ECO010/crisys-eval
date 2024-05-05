package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
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
    private AnchorPane anchorPane;

    @FXML
    private Button backBtn;

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

    private Scene scene;

    // DAO Objects
    private final ICSAssetVulnerabilityDAO icsAssetVulnerabilityDAO = new ICSAssetVulnerabilityDAO();
    private final AttackPatternDAO attackPatternDAO = new AttackPatternDAO();
    private final AttackStepDAO attackStepDAO = new AttackStepDAO();

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
/*

    public void initializeLayoutListeners() {
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            updateLayout((double) newValue, scene.getHeight());
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            updateLayout(scene.getWidth(), (double) newValue);
        });
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        initializeLayoutListeners();
        attachLayoutListeners();
    }

    public void attachLayoutListeners() {
        if (scene != null) {
            scene.widthProperty().addListener((observable, oldValue, newValue) -> {
                updateLayout((double) newValue, scene.getHeight());
            });
            scene.heightProperty().addListener((observable, oldValue, newValue) -> {
                updateLayout(scene.getWidth(), (double) newValue);
            });
        }
    }

    private void updateLayout(double newWidth, double newHeight) {
        // Update the layout of the elements based on the new window size
        anchorPane.setPrefWidth(newWidth);
        anchorPane.setPrefHeight(newHeight);

        // Set the width of the evaluationTable
        evaluationTable.setPrefWidth(newWidth * 0.75);
        systemName.setPrefWidth(newWidth * 0.3);
        evalDT.setPrefWidth(newWidth * 0.3);
        evaluationScore.setPrefWidth(newWidth * 0.3);
        evaluationID.setPrefWidth(newWidth * 0.3);

        // Adjust the layout of other elements in the scene
        deleteButton.setLayoutX((newWidth - deleteButton.getPrefWidth()) / 2);
        loadButton.setLayoutX((newWidth - loadButton.getPrefWidth()) / 2);
        backBtn.setLayoutX((newWidth - backBtn.getPrefWidth()) / 2);
    }
*/

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
            alert.setContentText("Please select evaluation(s) to delete.");
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

    @FXML
    private void onLoadClick() throws IOException {
        Evaluation selectedEvaluation = evaluationTable.getSelectionModel().getSelectedItem();
        // Get the selected evaluations from the TableView
        ObservableList<Evaluation> selectedEvaluations = evaluationTable.getSelectionModel().getSelectedItems();

        // Check there is an evaluation selected
        if (!selectedEvaluations.isEmpty()) {
            // Retrieve the EvaluationData instance for the selected evaluation
            currentEvaluation = evaluationDAO.retrieveEvaluationData(selectedEvaluation.getEvaluationID());
            currentEvaluation.setEvaluationID(selectedEvaluation.getEvaluationID());
            retrievedEvaluationAssets = evaluationDAO.retrieveEvaluationAssetData(selectedEvaluation.getEvaluationID());
            yearFrom = evaluationDAO.getTreeYearFrom(selectedEvaluation.getEvaluationID());
            yearTo = evaluationDAO.getTreeYearTo(selectedEvaluation.getEvaluationID());

            if (selectedEvaluations.size() == 1) {
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
                TreeItem<String> rootItem = generateAttackTree(currentEvaluation, retrievedEvaluationAssets, yearFrom, yearTo);

                TreeViewSceneController treeViewController = treeViewLoader.getController();

                // don't attach context menu
                treeViewController.setRootNode(rootItem, false);
                treeViewController.getYearFrom(yearFrom);
                treeViewController.getYearTo(yearTo);

                // Create a new window for each scene and display them side by side
                Stage evalEndStage = new Stage();
                evalEndStage.setTitle(evalEndController.SCENE_TITLE);
                evalEndStage.setScene(new Scene(evalEndRoot));
                evalEndStage.show();

                Stage treeViewStage = new Stage();
                treeViewStage.setTitle(treeViewController.SCENE_TITLE);
                treeViewStage.setScene(new Scene(treeViewRoot));
                //treeViewStage.show();
                treeViewStage.toBack();

                DataManager.getInstance().addOpenStage(treeViewStage);
                DataManager.getInstance().addOpenStage(evalEndStage);

                // Make continue button invisible on tree view scene, all they can do from here is save as PDF
                treeViewController.continueBtn.setVisible(false);
            }
            // Can only load one evaluation, error if more than one is selected
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Only one evaluation can be loaded at a time.");
                alert.showAndWait();
            }
        }
        // error as no evaluation has been selected
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select an evaluation to load.");
            alert.showAndWait();
        }
    }

    // Ended up having to generate the tree again
    public TreeItem<String> generateAttackTree(Evaluation currentEvaluation, List<EvaluationAsset> evaluationAssets, int yearFrom, int yearTo) {
        // Root of the tree, the evaluated system name. Is hidden on the tree view
        TreeItem<String> rootItem = new TreeItem<>(currentEvaluation.getCriticalSystemName());

        // Go through evaluation assets passed from previous scene and get the asset type and the asset name
        for (EvaluationAsset evaluationAsset : evaluationAssets) {
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

                    // This is a list of mitigations to be added as a child to the CWE
                    List<WeaknessMitigation> cweMitigations = weaknessEnumeration.getWeaknessMitigations();
                    if (!cweMitigations.isEmpty()) {
                        for (WeaknessMitigation cweMitigation :
                                cweMitigations) {
                            TreeItem<String> mitigationItem = new TreeItem<>(cweMitigation.toString());
                            cweItem.getChildren().add(mitigationItem);
                        }
                    }

                    // Get the list of CapecIds from the CWE Item
                    List<Integer> relatedCapecIds = attackPatternDAO.getCapecIdsFromCwe(weaknessEnumeration.getCweId());

                    // Get list of attack pattern objects from capec Ids
                    List<AttackPattern> relatedAttackPatterns = attackPatternDAO.getAttackPatternsFromIds(relatedCapecIds);

                    // Loop through list of related attack patterns and add each one as a child to the relevant cwe Item if it isn't empty
                    if (!relatedAttackPatterns.isEmpty()) {
                        for (AttackPattern attackPattern : relatedAttackPatterns) {
                            TreeItem<String> attackPatternItem = new TreeItem<>(attackPattern.toString());
                            cweItem.getChildren().add(attackPatternItem);

                            // This is a list of mitigations to be added as a child to the CWE
                            List<Mitigation> capecMitigations = attackPattern.getMitigations();
                            if (!capecMitigations.isEmpty()) {
                                for (Mitigation capecMitigation :
                                        capecMitigations) {
                                    TreeItem<String> mitigationItem = new TreeItem<>(capecMitigation.toString());
                                    attackPatternItem.getChildren().add(mitigationItem);
                                }
                            }

                            // Get the execution flow for each attack
                            List<AttackStep> attackSteps = attackStepDAO.getAttackSteps(attackPattern.getCapecId());

                            if (!attackSteps.isEmpty()) {
                                for (AttackStep attackStep: attackSteps) {
                                    TreeItem<String> attackStepItem = new TreeItem<>(attackStep.toString());
                                    attackPatternItem.getChildren().add(attackStepItem);

                                    // Get the step techniques for each step
                                    List<AttackStepTechnique> stepTechniques = attackStepDAO.getAttackStepTechniques(attackPattern.getCapecId(), attackStep.getStep());
                                    if (!stepTechniques.isEmpty()) {
                                        for (AttackStepTechnique stepTechnique: stepTechniques) {
                                            TreeItem<String> attackStepTechniqueItem = new TreeItem<>(stepTechnique.toString());
                                            attackStepItem.getChildren().add(attackStepTechniqueItem);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return rootItem;
    }
}
