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
import java.util.*;

// TODO:
//  *** Save Tree Data (Include mitigations as output) ***
//  Clean up (get rid of personal info and briefly clean code), package and submit (WARNING: Loading FXML document with JavaFX API of version 21 by JavaFX runtime of version 11.0.1)

public class PreparednessWindowController {
    public final String SCENE_TITLE = "Preparedness Survey";

    @FXML
    private Button backToTreeViewBtn;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Label assetInfoLabel;

    @FXML
    private Button continueBtn;

    // Initialize DAOs to query the DB
    private final EvaluationDAO evaluationDAO = new EvaluationDAO();
    private final ICSAssetVulnerabilityDAO icsAssetVulnerabilityDAO = new ICSAssetVulnerabilityDAO();
    private final CommonWeaknessEnumerationDAO commonWeaknessEnumerationDAO =  new CommonWeaknessEnumerationDAO();
    private final AttackPatternDAO attackPatternDAO = new AttackPatternDAO();

    private String selectedOption;
    private double systemSafetyScore;
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

    // Get the number of potential mitigations for all CWEs and CAPECs linked to the asset
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
        return numMitigations;
    }

    // Should just bring treeView into focus
    // Bring the tree view stage to the front by moving this one to the back
    @FXML
    private void onBackClick() {
        Stage stage = (Stage) backToTreeViewBtn.getScene().getWindow();
        stage.toBack();
    }

    // Navigate to the window/scene with the security preparedness score
    // Close both scenes: The TreeView Scene and the PreparednessWindowScene
    @FXML
    private void onContinueClick(ActionEvent event) {
        // Make sure combo box isn't null before calculation
        // check if selected option has a value, if not error
        if (selectedOption == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select an option");
            alert.showAndWait();
        }
        else {
            // Calculate security score for Current Asset and update the DB
            EvaluationAsset currentAsset = retrievedEvaluationAssets.get(currentAssetIndex);
            calculateAssetSafetyScore(currentAsset.getAssetName(), currentAsset.getEvaluationID());
            currentAsset.setAssetSafetyScore(evaluationDAO.getAssetSafetyScore(currentAsset.getEvaluationID(), currentAsset.getAssetName()));

            // Clear the selected option from combo box so the user can respond for the next asset
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
                systemSafetyScore = evaluationDAO.getSystemSafetyScore(currentAsset.getEvaluationID());
                currentEvaluation.setEvaluationScore(systemSafetyScore);
                currentEvaluation.setEvaluationID(evaluationDAO.getLatestEvalID());
                System.out.println("System safety score is: " + systemSafetyScore);

                // Navigate to Evaluation End Scene/Window
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("evaluation-end.fxml"));
                    Parent root = loader.load();

                    // Get the controller of the Evaluation End
                    EvaluationEndController evaluationEndController = loader.getController();

                    // Send data across
                    evaluationEndController.getCurrentEvaluation(currentEvaluation);
                    evaluationEndController.getEvaluationAssets(retrievedEvaluationAssets);
                    evaluationEndController.getSystemSafetyScore(systemSafetyScore);
                    evaluationEndController.getGeneratedTree(attackTreeView);
                    evaluationEndController.getYearFrom(yearFrom);
                    evaluationEndController.getYearTo(yearTo);
                    evaluationEndController.updateProgress(systemSafetyScore);
                    evaluationEndController.updateHeading();

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
    }

    // Total Asset Safety Score Out of Hundred
    // Split into 4 categories:
    // Survey Answer (out of 10) -> 1st
    // Average CVSS Scores (out of 30) -> 2nd
    // Total number of attack patterns linked to the asset (out of 30) -> 3rd
    // Average EPSS score for CVEs linked to CWE (out of 30) -> 4th
    // (the EPSS score represents the probability [0-1] of exploitation in the wild in the next 30 days (following daily score publication))
    private void calculateAssetSafetyScore(String currentAssetName, int evaluationId) {
        TreeItem<String> assetNode = attackTreeView.getRoot().getChildren().get(currentAssetIndex);
        String assetType = evaluationDAO.getAssetTypeFromAssetName(evaluationId, assetNode.getValue());
        // Get the list of CWEs under the asset node
        List<TreeItem<String>> cweNodes = assetNode.getChildren();

        if (!cweNodes.isEmpty()) {
            int numOfLinkedAttackPatterns = 0;
            int numOfLinkedCVEsToAsset = 0;
            Double averageCVSSTotal = 0.0;
            Double averageEPSSTotal = 0.0;

            // Loop through each CWE node
            for (TreeItem<String> cweNode : cweNodes) {

                // Get the list of Attack Patterns (CAPECs) under the CWE node
                List<TreeItem<String>> allCapecNodes = cweNode.getChildren();

                // Storing CAPECs and CVEs as Sets because they automatically remove duplicates
                Set<String> uniqueCVEs = new HashSet<>(); // Storing unique CVEs
                Set<Integer> uniqueAttackPatterns = new HashSet<>(); // Storing unique CAPECs

                // Go through each capec node
                for (TreeItem<String> capecNode : allCapecNodes) {
                    // Get the display name and change it to an Attack Pattern object
                    String capecDisplayName = capecNode.getValue();
                    AttackPattern attackPattern = AttackPattern.fromStringToAttackPattern(capecDisplayName);
                    int capecId = attackPattern.getCapecId();

                    // Check likelihood and add to the count if it's high or medium
                    String likelihood = attackPatternDAO.getAttackLikelihoodFromDB(capecId);
                    if (likelihood.equalsIgnoreCase("High") || likelihood.equalsIgnoreCase("Medium")) {
                        uniqueAttackPatterns.add(capecId);
                    }
                }
                // Update the count of linked patterns with the size of the set (which automatically removes duplicates)
                numOfLinkedAttackPatterns += uniqueAttackPatterns.size();

                // Go through CWE, get the score, the total score / number of CWEs to get the score for this section
                // Get the name of the weakness
                // access the CWE node and count the mitigations by retrieving the CWE object associated with the node
                String cweDisplayName = cweNode.getValue();
                CommonWeaknessEnumeration cwe = CommonWeaknessEnumeration.fromStringToCWE(cweDisplayName);
                String cweId = cwe.getCweId();

                // get the average cvss score for each CWE linked to the asset node and add them
                averageCVSSTotal += icsAssetVulnerabilityDAO.getAverageCVSSForCWE(cweId, yearFrom, yearTo);

                // for each CWE, get the linked CVEs
                String linkedCVEs = commonWeaknessEnumerationDAO.getLinkedCVEs(cweId, assetType, yearFrom, yearTo);

                // CVEs come as a single string with commas (i.e. CVE2024-xx, CVE2023-xx).
                // Split by commas
                // loop through each split CVE and get the EPSS score from the DB (WHERE CVENumber = 'value we get from splitting')
                // update the value of total CVEs linked (NumOfLinkedCVEsToAsset)
                // update the value of averageEPSSTotal with the value fetched from each CVE
                if (!linkedCVEs.isEmpty()) {
                    String[] cveArray = linkedCVEs.split(",\\s*");

                    // Add CVEs to the Unique CVE set
                    uniqueCVEs.addAll(Arrays.asList(cveArray));

                    // set the number of linked CVEs
                    numOfLinkedCVEsToAsset += uniqueCVEs.size();

                    // Query the database to retrieve total EPSS score for CVEs linked to each CWE
                    averageEPSSTotal = icsAssetVulnerabilityDAO.getTotalEPSSForLinkedCVEs(Arrays.asList(cveArray));
                }
            }

            // TESTING VALUES
            System.out.println("average cvss total for asset: " + currentAssetName + " is: " + averageCVSSTotal);
            System.out.println("average epss total for asset " + currentAssetName + " is: " + averageEPSSTotal);
            System.out.println("asset " + currentAssetName + " has: " + numOfLinkedCVEsToAsset + " CVEs");
            // divide the total of the averages by the number of CWEs
            Double finalCVSSAverageForAsset = averageCVSSTotal / cweNodes.size();
            System.out.println("final cvss average to categorize for asset: " + currentAssetName + " is: " + finalCVSSAverageForAsset);

            // 2nd Category: Average CVSS v3.x specifications score rating (for each CWE linked to the Asset) (out of 30):
            // update security safety score based on the criteria
            if (finalCVSSAverageForAsset == 0.0) {
                currentAssetSafetyScore += 30;
            }
            else if (finalCVSSAverageForAsset >= 0.01 && finalCVSSAverageForAsset <= 3.9) {
                currentAssetSafetyScore += 25;
            }
            else if (finalCVSSAverageForAsset >= 4.0 && finalCVSSAverageForAsset <= 6.9) {
                currentAssetSafetyScore += 20;
            }
            else if (finalCVSSAverageForAsset >= 7.0 && finalCVSSAverageForAsset <= 8.9) {
                currentAssetSafetyScore += 15;
            }
            else if (finalCVSSAverageForAsset >= 9.0 && finalCVSSAverageForAsset <= 10.0) {
                currentAssetSafetyScore += 10;
            }

            // 3rd Category: Total Number of high and medium likelihood CAPECs linked to the Asset (Asset attack surface) (out of 30)??
            // Check the number of linked patterns and update the score
            System.out.println("The number of linked attack patterns for " + currentAssetName + " is " + numOfLinkedAttackPatterns);

            // Multiplier depending on the years to filter selected by the user
            int multiplier = (yearTo - yearFrom) + 1;

            if (numOfLinkedAttackPatterns == 0) {
                currentAssetSafetyScore += 30;
            }
            else if (numOfLinkedAttackPatterns > 0 && numOfLinkedAttackPatterns <= 50 * multiplier) {
                currentAssetSafetyScore += 25;
            }
            else if (numOfLinkedAttackPatterns > 50 * multiplier && numOfLinkedAttackPatterns < 100 * multiplier) {
                currentAssetSafetyScore += 20;
            }
            else if (numOfLinkedAttackPatterns >= 100 * multiplier) {
                currentAssetSafetyScore += 15;
            }

            // 4th Category: Average EPSS score for all CVEs linked to CWE in the timeframe selected
            // (the score representing the probability [0-1] of exploitation in the wild in the next 30 days (following score publication))
            // multiply EPSS score by 100 to give a percent rating
            // weighting my score rating similar to CVSS
            // Very High: 90 - 100, asset score: +10
            // High: 70 - 89, asset score: +15
            // Medium: 40 - 69, asset score: +20
            // Low: 1 - 39, asset score: +25
            // None: < 1, asset score: +30
            Double finalEpssAverageForAsset = averageEPSSTotal / numOfLinkedCVEsToAsset;
            System.out.println("final epss average to categorize for asset: " + currentAssetName + " is: " + finalEpssAverageForAsset);
            if (finalEpssAverageForAsset < 1) {
                currentAssetSafetyScore += 30;
            }
            else if (finalEpssAverageForAsset >= 1 && finalCVSSAverageForAsset < 40) {
                currentAssetSafetyScore += 25;
            }
            else if (finalEpssAverageForAsset >= 40 && finalCVSSAverageForAsset < 70) {
                currentAssetSafetyScore += 20;
            }
            else if (finalEpssAverageForAsset >= 70 && finalCVSSAverageForAsset < 90) {
                currentAssetSafetyScore += 15;
            }
            else if (finalEpssAverageForAsset >= 90 && finalCVSSAverageForAsset < 100) {
                currentAssetSafetyScore += 10;
            }
        }
        // Exception case: Assets without CWEs automatically get a safety score of 90 only leaving the 10 to be added from the user's response.
        // These are theoretically the safest because they have no linked weaknesses, hence no linked attack patterns and CVEs (exploitation examples)
        else {
            currentAssetSafetyScore += 90;
        }
        // 1st Category: Survey Answer, happens regardless of linked CWEs
        // NOT SECURE
        if (Objects.equals(selectedOption, secureOptionsList.get(0))) {
            currentAssetSafetyScore += 2;
        }
        // SLIGHTLY SECURE
        else if (Objects.equals(selectedOption, secureOptionsList.get(1))) {
            currentAssetSafetyScore += 4;
        }
        // MODERATELY SECURE
        else if (Objects.equals(selectedOption, secureOptionsList.get(2))) {
            currentAssetSafetyScore += 6;
        }
        // SECURE
        else if (Objects.equals(selectedOption, secureOptionsList.get(3))) {
            currentAssetSafetyScore += 8;
        }
        // VERY SECURE
        else if (Objects.equals(selectedOption, secureOptionsList.get(4))) {
            currentAssetSafetyScore += 10;
        }

        // Scores have been calculated update the DB
        evaluationDAO.updateAssetScore(currentAssetSafetyScore, currentAssetName, evaluationId);
        // Testing score output
        System.out.println("The score for Asset: " + currentAssetName + " is: " + currentAssetSafetyScore);
        // reset the score calculator
        currentAssetSafetyScore = 0;
    }

    // total scores of assets / num of assets
    // update using the eval ID of the 1st asset as it will be the same for all.
    private void calculateSystemSafetyScore() {
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
