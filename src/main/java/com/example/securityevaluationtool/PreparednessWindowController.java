package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.AttackPattern;
import com.example.securityevaluationtool.database.CommonWeaknessEnumeration;
import com.example.securityevaluationtool.database.Evaluation;
import com.example.securityevaluationtool.database.EvaluationAsset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PreparednessWindowController {
    public final String SCENE_TITLE = "Preparedness Survey";

    @FXML
    private Button backToTreeViewBtn;

    @FXML
    private Button previousAssetBtn;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Label assetInfoLabel;

    @FXML
    private Button continueBtn;

    private String selectedOption;

    List<String> secureOptionsList = new ArrayList<>(Arrays.asList("Not Secure", "Slightly Secure", "Moderately Secure", "Secure", "Very Secure"));

    // Field(s) and method(s) for getting data from previous controller
    private Evaluation currentEvaluation;
    private List<EvaluationAsset> retrievedEvaluationAssets;
    private TreeView<String> attackTreeView;

    public void getCurrentEvaluation(Evaluation currentEvaluation) {
        this.currentEvaluation = currentEvaluation;
    }

    public void getEvaluationAssets(List<EvaluationAsset> retrievedEvaluationAssets) {
        this.retrievedEvaluationAssets = retrievedEvaluationAssets;
    }

    public void getGeneratedTree(TreeView<String> attackTreeView) {
        this.attackTreeView = attackTreeView;
    }

    // track current asset
    private int currentAssetIndex = 0;

    @FXML
    private void initialize() {
        ObservableList<String> observableList = FXCollections.observableList(secureOptionsList);
        comboBox.setItems(observableList);
    }

    // Call this method after setting the necessary data
    public void initializeWithData() {
        // Initialize UI with information about the first retrieved asset
        updateAssetInfo();
    }

    @FXML
    private void onComboBoxClick() {
        selectedOption = comboBox.getValue();
    }

    private void updateAssetInfo() {
        if (currentAssetIndex <= retrievedEvaluationAssets.size() - 1) {
            EvaluationAsset currentAsset = retrievedEvaluationAssets.get(currentAssetIndex);
            // Update assetInfoLabel with information about the current asset
            assetInfoLabel.setText("Considering the " + getNumOfMitigations() + " potential mitigations suggested for protecting " + currentAsset.getAssetName());
        }
    }

    private int getNumOfMitigations() {
        // Use the attackTreeView to count the number of mitigations for the specified asset
        // Implement logic to traverse the tree and count mitigations
        int numMitigations = 0;
        TreeItem<String> assetNode = attackTreeView.getRoot().getChildren().get(currentAssetIndex);

        // Get the list of CWEs under the asset node
        List<TreeItem<String>> cweNodes = assetNode.getChildren();

        // Loop through each CWE node
        for (TreeItem<String> cweNode : cweNodes) {
            // Get the name of the weakness
            // access the CWE node and count the mitigations by retrieving the CWE object associated with the node
            String cweDisplayName = cweNode.getValue();
            CommonWeaknessEnumeration cwe = CommonWeaknessEnumeration.fromStringToCWE(cweDisplayName);

            // Now, count the mitigations for this CWE and add it
            assert cwe != null;
            numMitigations += cwe.getWeaknessMitigations().size();

            // Get the list of Attack Patterns (CAPECs) under the CWE node
            List<TreeItem<String>> capecNodes = cweNode.getChildren();

            // Loop through each CAPEC node to count the mitigations
            for (TreeItem<String> capecNode : capecNodes) {
                // count num of mitigations by retrieving the AttackPattern object associated with the CAPEC node
                String capecDisplayName = capecNode.getValue();
                AttackPattern attackPattern = AttackPattern.fromStringToAttackPattern(capecDisplayName);

                // Now, you can count the mitigations for this attack pattern
                assert attackPattern != null;
                numMitigations += attackPattern.getMitigations().size();
            }
        }
        // Output the number of mitigations linked to the asset
        System.out.println("Asset Name: " + assetNode.getValue() + ", Mitigations: " + numMitigations);
        return numMitigations; // Placeholder
    }

    @FXML
    private void onBackClick() {
        // Close the current scene
        Stage stage = (Stage) backToTreeViewBtn.getScene().getWindow();
        stage.close();
    }

    // Navigate to the window/scene with the security preparedness score
    // Close both scenes: The TreeView Scene and the PreparednessWindowScene
    @FXML
    private void onContinueClick(ActionEvent event) {
        // Calculate security score for Current Asset and update the DB
        EvaluationAsset currentAsset = retrievedEvaluationAssets.get(currentAssetIndex);
        calculateAssetSafetyScore(currentAsset.getAssetName(), currentAsset.getEvaluationID());

        // Clear the selected option from combo box
        comboBox.getSelectionModel().clearSelection();

        // Increment current asset index
        currentAssetIndex++;
        // Handle navigation to the next retrieved asset
        if (currentAssetIndex <= retrievedEvaluationAssets.size() - 1) {
            updateAssetInfo();
        }
        else {
            System.out.println(currentEvaluation.getCriticalSystemName());
            System.out.println(retrievedEvaluationAssets.size());
            //System.out.println(retrievedEvaluationAssets.get(0).getAssetName());

            // No more assets available, handle next action
            // Close the current scene (preparedness window)
            Stage stage = (Stage) continueBtn.getScene().getWindow();
            stage.close();

            // Close Tree view
            closeTreeViewScene();

            // Calculate System Safety score and pass it to the next controller
            calculateSystemSafetyScore();

            // Navigate to Evaluation End Scene/Window
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("evaluation-end.fxml"));
                Parent root = loader.load();

                // Get the controller of the Evaluation End
                EvaluationEndController evaluationEndController = loader.getController();

                // Get the current stage from the event source
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the scene of the current stage to the Evaluation End
                currentStage.setScene(new Scene(root));
                currentStage.setTitle(evaluationEndController.SCENE_TITLE);

                // Show the stage if it's not already showing
                if (!currentStage.isShowing()) {
                    currentStage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle loading error
            }
        }
    }

    // Total Asset Safety Score Out of Hundred
    // Split into 4 categories:
    // Survey Answer (out of 20)
    // Average CVSS Scores (out of 30)
    // Total number of mitigations (out of 30)
    // Weakness Likelihood of Exploit (out of 20)
    private void calculateAssetSafetyScore(String currentAssetName, int evaluationId) {
        int assetScore = 0;
        // NOT SECURE (out of 20)
        if (Objects.equals(selectedOption, secureOptionsList.get(0))) {
            assetScore += 0;
        }
        // SLIGHTLY SECURE (out of 20)
        else if (Objects.equals(selectedOption, secureOptionsList.get(1))) {
            assetScore += 5;
        }
        // MODERATELY SECURE (out of 20)
        else if (Objects.equals(selectedOption, secureOptionsList.get(2))) {
            assetScore += 10;
        }
        // SECURE (out of 20)
        else if (Objects.equals(selectedOption, secureOptionsList.get(3))) {
            assetScore += 15;
        }
        // VERY SECURE (out of 20)
        else if (Objects.equals(selectedOption, secureOptionsList.get(4))) {
            assetScore += 20;
        }

        // Assets without CWEs automatically get a safety score of 100.

        // Average CVSS v3.x specifications score rating (for each CWE linked to the Asset) (out of 30):
        // Critical: 9.0 - 10, asset score: +6
        // High: 7.0 - 8.9, asset score: +12
        // Medium: 4.0 - 6.9, asset score: +18
        // Low: 0.1 - 3.9, asset score: +24
        // None: 0.0, asset score: +30
        // Go through CWE, get the score, the total score / number of CWEs to get the score for this section

        // CWE Likelihood of exploit (Out of 20), There is 'none' so this score can't be perfect
        // High, asset score: +5
        // Medium, asset score: +10
        // Low, asset score: +15
        // Unknown, asset score: +10
        // Go through each CWE, get the score, the total score / number of CWEs to get the score for this section

        // Total Number of Mitigations recommended for the Asset (out of 30)??

    }

    private void calculateSystemSafetyScore() {
        // total scores of assets / num of assets
    }

    private void closeTreeViewScene() {
        // Get a list of all open windows
        List<Window> openWindows = Window.getWindows();

        // Get the controller of the Evaluation End
        TreeViewSceneController treeViewSceneController = new TreeViewSceneController();

        // Iterate through the open windows and close all open windows
        for (Window window : openWindows) {
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                if (stage.getTitle().equals(treeViewSceneController.SCENE_TITLE)) {
                    stage.close();
                    break;
                }
            }
        }
    }
}
