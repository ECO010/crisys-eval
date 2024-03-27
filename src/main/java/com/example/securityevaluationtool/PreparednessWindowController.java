package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.*;
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

    EvaluationDAO evaluationDAO = new EvaluationDAO();

    private String selectedOption;

    List<String> secureOptionsList = new ArrayList<>(Arrays.asList("Not Secure", "Slightly Secure", "Moderately Secure", "Secure", "Very Secure"));

    // Field(s) and method(s) for getting data from previous controller
    private Evaluation currentEvaluation;
    private List<EvaluationAsset> retrievedEvaluationAssets;
    private TreeView<String> attackTreeView;
    private int yearTo;
    private int yearFrom;

    public void getYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    public void getYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

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
    private int currentAssetIndex;
    private int currentAssetSafetyScore;

    @FXML
    private void initialize() {
        ObservableList<String> observableList = FXCollections.observableList(secureOptionsList);
        comboBox.setItems(observableList);
    }

    // Call this method after setting the necessary data
    public void initializeWithData() {
        currentAssetSafetyScore = 0;
        currentAssetIndex = 0;
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

    // Should just move window behind tree view not really close
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
    // Survey Answer (out of 25)
    // Average CVSS Scores (out of 25)
    // Total number of attack patterns linked to the asset (out of 25)
    // Weakness Likelihood of Exploit (out of 25)***
    // EPSS score linked to CVE's linked to CWE (the score representing the probability [0-1] of exploitation in the wild in the next 30 days (following score publication)) ??
    private void calculateAssetSafetyScore(String currentAssetName, int evaluationId) {
        TreeItem<String> assetNode = attackTreeView.getRoot().getChildren().get(currentAssetIndex);
        // Get the list of CWEs under the asset node
        List<TreeItem<String>> cweNodes = assetNode.getChildren();

        if (!cweNodes.isEmpty()) {
            int numOfLinkedAttackPatterns = 0;
            Double averageCVSSTotal = 0.0;

            // Loop through each CWE node
            for (TreeItem<String> cweNode : cweNodes) {

                // Get the list of Attack Patterns (CAPECs) under the CWE node
                List<TreeItem<String>> capecNodes = cweNode.getChildren();
                numOfLinkedAttackPatterns += capecNodes.size();

                // Go through CWE, get the score, the total score / number of CWEs to get the score for this section
                // Get the name of the weakness
                // access the CWE node and count the mitigations by retrieving the CWE object associated with the node
                String cweDisplayName = cweNode.getValue();
                CommonWeaknessEnumeration cwe = CommonWeaknessEnumeration.fromStringToCWE(cweDisplayName);
                String cweId = cwe.getCweId();

                // Initialize DAO to query the DB
                ICSAssetVulnerabilityDAO icsAssetVulnerabilityDAO = new ICSAssetVulnerabilityDAO();
                // get the average cvss score for each cwe and add them
                averageCVSSTotal += icsAssetVulnerabilityDAO.getAverageCVSSForCWE(cweId, yearFrom, yearTo);
            }
            // divide the total of the averages by the number of cwes
            Double finalCVSSAverageForAsset = averageCVSSTotal / cweNodes.size();
            // 3rd Category: Average CVSS v3.x specifications score rating (for each CWE linked to the Asset) (out of 30):
            // Critical: 9.0 - 10, asset score: +5
            // High: 7.0 - 8.9, asset score: +10
            // Medium: 4.0 - 6.9, asset score: +15
            // Low: 0.1 - 3.9, asset score: +20
            // None: 0.0, asset score: +25
            // update security safety score based on the criteria
            if (finalCVSSAverageForAsset == 0.0) {
                currentAssetSafetyScore += 25;
            }
            else if (finalCVSSAverageForAsset >= 0.1 && finalCVSSAverageForAsset <= 3.9) {
                currentAssetSafetyScore += 20;
            }
            else if (finalCVSSAverageForAsset >= 4.0 && finalCVSSAverageForAsset <= 6.9) {
                currentAssetSafetyScore += 15;
            }
            else if (finalCVSSAverageForAsset >= 7.0 && finalCVSSAverageForAsset <= 8.9) {
                currentAssetSafetyScore += 10;
            }
            else if (finalCVSSAverageForAsset >= 9.0 && finalCVSSAverageForAsset <= 10.0) {
                currentAssetSafetyScore += 5;
            }

            // 2nd Category: Total Number of CAPECs inked to the Asset (Asset attack surface) (out of 20)??
            // None (0) -> +25
            // Low (1 - 30) -> +20
            // Medium (31 - 99) -> +15
            // High (100+) -> +10
            // Check the number of linked patterns and update the score
            if (numOfLinkedAttackPatterns == 0) {
                currentAssetSafetyScore += 25;
            }
            else if (numOfLinkedAttackPatterns > 0 && numOfLinkedAttackPatterns <= 30) {
                currentAssetSafetyScore += 20;
            }
            else if (numOfLinkedAttackPatterns > 30 && numOfLinkedAttackPatterns < 100) {
             currentAssetSafetyScore += 15;
            }
            else if (numOfLinkedAttackPatterns >= 100) {
                currentAssetSafetyScore += 10;
            }
        }
        // Exception case: Assets without CWEs automatically get a safety score of 75 only leaving the 25 to be added from the user's response.
        // I'll probably skip asking the survey question for Assets without CWEs
        else {
            currentAssetSafetyScore += 75;
        }
        // 1st Category: Survey Answer, happens regardless of linked CWE's
        // NOT SECURE
        if (Objects.equals(selectedOption, secureOptionsList.get(0))) {
            currentAssetSafetyScore += 5;
        }
        // SLIGHTLY SECURE
        else if (Objects.equals(selectedOption, secureOptionsList.get(1))) {
            currentAssetSafetyScore += 10;
        }
        // MODERATELY SECURE
        else if (Objects.equals(selectedOption, secureOptionsList.get(2))) {
            currentAssetSafetyScore += 15;
        }
        // SECURE
        else if (Objects.equals(selectedOption, secureOptionsList.get(3))) {
            currentAssetSafetyScore += 20;
        }
        // VERY SECURE
        else if (Objects.equals(selectedOption, secureOptionsList.get(4))) {
            currentAssetSafetyScore += 25;
        }

        // Scores have been calculated update the DB
        evaluationDAO.updateAssetScore(currentAssetSafetyScore, currentAssetName, evaluationId);
        System.out.println("The score for Asset: " + currentAssetName + " is: " + currentAssetSafetyScore);
        // reset the score calculator
        currentAssetSafetyScore = 0;

        // CWE Likelihood of exploit (Out of 20), There is no 'none' so this score can't be perfect unless there isn't any linked CWE
        // Might need to change this to EPSS Likelihood
        // High, asset score: +5
        // Medium, asset score: +10
        // Low, asset score: +15
        // Unknown, asset score: +10
        // Go through each CWE, get the score, the total score / number of CWEs to get the score for this section
    }

    private void calculateSystemSafetyScore() {
        // total scores of assets / num of assets
        // get eval ID of the 1st asset as it will be the same for all.
        evaluationDAO.updateSystemSafetyScore(retrievedEvaluationAssets.get(0).getEvaluationID());
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
