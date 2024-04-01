package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.Evaluation;
import com.example.securityevaluationtool.database.EvaluationAsset;
import com.example.securityevaluationtool.database.EvaluationDAO;
import com.example.securityevaluationtool.database.ICSAssetVulnerabilityDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssetDeclarationController {

    public final String SCENE_TITLE = "Asset Declaration";

    @FXML
    private VBox mainVBox;

    @FXML
    private HBox sampleRow;

    @FXML
    private TextField assetNameField;

    @FXML
    private ComboBox<String> assetTypeComboBox;

    private String selectedOption;

    @FXML
    private Button addButton;

    @FXML
    private Button BackBtn;

    @FXML
    private Button ContinueBtn;

    @FXML
    private ScrollPane scrollPane;

    // DAOs
    private final EvaluationDAO evaluationDAO = new EvaluationDAO();
    private final ICSAssetVulnerabilityDAO icsAssetVulnerabilityDAO = new ICSAssetVulnerabilityDAO();

    // Fields
    private String assetType;
    private String assetName;

    // Field(s) and method(s) for getting data from previous controller
    private Evaluation currentEvaluation;
    public void setCurrentEvaluation(Evaluation currentEvaluation) {
        this.currentEvaluation = currentEvaluation;
    }

    @FXML
    private void initialize() {
        scrollPane.setFitToWidth(true);
    }

    @FXML
    private void addRow() {
        HBox newRow = new HBox();

        // Create a TextField for the asset name
        TextField assetNameField = new TextField();
        assetNameField.setPrefHeight(30);
        assetNameField.setPrefWidth(182);
        assetNameField.setPromptText("Asset Name");

        // Create a ComboBox for the asset type
        ComboBox<String> assetTypeComboBox = new ComboBox<>();
        List<String> assetTypes = icsAssetVulnerabilityDAO.getAssetTypes();
        ObservableList<String> assetOptions = FXCollections.observableList(assetTypes);
        assetTypeComboBox.setItems(assetOptions);
        assetTypeComboBox.setPrefHeight(30);
        assetTypeComboBox.setPrefWidth(419);
        assetTypeComboBox.setPromptText("Asset Type");

        // Set Combo Box Value
        onComboBoxClick(assetTypeComboBox);

        // Add the TextField and ComboBox to the new row
        newRow.getChildren().addAll(assetNameField, assetTypeComboBox);

        mainVBox.getChildren().add(newRow);
    }

    // TODO: Add a delete row function

    // SET COMBO BOX VALUE AFTER SELECTION
    private void onComboBoxClick(ComboBox<String> comboBox) {
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedOption = newValue;
            System.out.println("Selected value: " + selectedOption);
        });
    }

    // TODO: Save and insert data into eval and evalAsset tables
    // Check that there are assets actually entered (text field should not be blank and combo box should have a selected value)
    // Move to a screen that says what's happening next (i.e. tree being generated in the form of a tree view which is a folder like structure.
    // Once the tree is generated you can save it as a pdf for further review and interact with the tree nodes (attack patterns and weaknesses)
    // Confirm that all assets have been added
    // See if tree can be generated in the background then the user clicks continue to view tree
    @FXML
    private void onContinueClick(ActionEvent event) {
        boolean isAnyFieldEmpty = false;
        List<EvaluationAsset> evaluationAssetsToSave = new ArrayList<>();

        // Check if any of the fields are empty
        for (Node node : mainVBox.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                for (Node childNode : row.getChildren()) {
                    if (childNode instanceof TextField) {
                        TextField textField = (TextField) childNode;
                        assetName = textField.getText().trim();
                        System.out.println("Asset Name: " + assetName);
                        if (assetName.isEmpty()) {
                            isAnyFieldEmpty = true;
                            break;
                        }
                    }
                    else if (childNode instanceof ComboBox) {
                        ComboBox<String> comboBox = (ComboBox<String>) childNode;
                        assetType = comboBox.getValue();
                        System.out.println("Combo box selected value: " + assetType);
                        if (assetType == null || assetType.isEmpty()) {
                            isAnyFieldEmpty = true;
                            break;
                        }
                    }
                }
                if (isAnyFieldEmpty) {
                    break;
                }

                // No fields are empty, create an EvaluationAsset object and set its fields
                EvaluationAsset evaluationAsset = new EvaluationAsset();
                evaluationAsset.setEvaluationID(evaluationDAO.getLatestEvalID());
                evaluationAsset.setAssetName(assetName);
                evaluationAsset.setAssetType(assetType);

                // Add the evaluationAsset to the list of assets to be saved
                evaluationAssetsToSave.add(evaluationAsset);
            }
        }

        // If any fields are empty, error
        if (isAnyFieldEmpty) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
        }
        // If no asset is added, error
        else if (mainVBox.getChildren().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please add at least one asset to continue with the evaluation.");
            alert.showAndWait();
        }
        else {
            // Confirm the assets, once we leave this page we can't come back, If they cancel clear assets to be saved until they click continue again
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Confirm Saving Assets");
            confirmAlert.setContentText("Are you sure you want to save the assets and continue?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Save the assets
                evaluationDAO.saveEvaluationAssets(evaluationAssetsToSave);
                System.out.println("Assets saved successfully.");

                // Navigate to the Tree Prompt Screen
                System.out.println("All fields filled. Navigating.");

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("tree-prompt-scene.fxml"));
                    Parent root = loader.load();

                    TreePromptSceneController treePromptSceneController = loader.getController();

                    // Send data to the tree prompt screen as this is where tree generation is done.
                    treePromptSceneController.getCurrentEvaluation(currentEvaluation);
                    treePromptSceneController.getEvaluationAssets(evaluationAssetsToSave);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle(treePromptSceneController.SCENE_TITLE);
                    stage.show();

                    // Close the current scene if needed
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    currentStage.close(); // Close instead of hide
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle loading error
                }
            }
            else {
                // User canceled, Clear the evaluation assets list to stop duplicate assets from being added
                evaluationAssetsToSave.clear();
            }
        }
    }
}
