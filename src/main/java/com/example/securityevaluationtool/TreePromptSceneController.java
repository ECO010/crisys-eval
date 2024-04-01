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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Explain Attack Trees and this tree generation briefly (tree generation, CWE -> CAPEC -> Mitigations),
 * Explain that it uses CISA ICS Advisory which goes back to 2010
 * Require them to select a filter for the timeframe (years) and then proceed to generate the tree
 * Need EvalId passed across, need way to fetch all assets linked to this evalId, need a way to get all distinct cweIds for each asset based on Asset Type
 * generate tree by fetching:
 * system name for current evalId (root node) -> all assets linked to the evalId -> all distinct CweId's linked to the assets -> all CapecIds linked to the CweId's
 * Navigate to Tree View Screen
 */

public class TreePromptSceneController {

    public final String SCENE_TITLE = "Tree Prompt Scene";
    List<String> yearOptionsList = new ArrayList<>(Arrays.asList("This Year", "Last Year", "Last 5 Years", "All Time"));

    @FXML
    private ComboBox<String> comboBox;

    private String selectedOption;
    private int yearTo;
    private int yearFrom;
    private String assetType;
    private String assetName;

    // DAO Objects
    private final ICSAssetVulnerabilityDAO icsAssetVulnerabilityDAO = new ICSAssetVulnerabilityDAO();
    private final AttackPatternDAO attackPatternDAO = new AttackPatternDAO();
    private final EvaluationDAO evaluationDAO = new EvaluationDAO();

    // Field(s) and method(s) for getting data from previous controller
    private Evaluation currentEvaluation;
    private List<EvaluationAsset> retrievedEvaluationAssets;
    public void getCurrentEvaluation(Evaluation currentEvaluation) {
        this.currentEvaluation = currentEvaluation;
    }
    public void getEvaluationAssets(List<EvaluationAsset> retrievedEvaluationAssets) {
        this.retrievedEvaluationAssets = retrievedEvaluationAssets;
    }

    @FXML
    private void initialize() {
        ObservableList<String> observableList = FXCollections.observableList(yearOptionsList);
        comboBox.setItems(observableList);
    }

    @FXML
    private void onComboClick() {
        selectedOption = comboBox.getValue();
        System.out.println("Combo box selected value: " + selectedOption);

        // This Year selected
        if (Objects.equals(selectedOption, yearOptionsList.get(0))) {
            yearFrom = Calendar.getInstance().get(Calendar.YEAR);
            yearTo = Calendar.getInstance().get(Calendar.YEAR);
            System.out.println(yearFrom);
            System.out.println(yearTo);
        }
        // Last Year selected
        else if (Objects.equals(selectedOption, yearOptionsList.get(1))) {
            yearFrom = Calendar.getInstance().get(Calendar.YEAR) - 1;
            yearTo = Calendar.getInstance().get(Calendar.YEAR);
            System.out.println(yearFrom);
            System.out.println(yearTo);
        }
        // Last 5 Years selected
        else if (Objects.equals(selectedOption, yearOptionsList.get(2))) {
            yearFrom = Calendar.getInstance().get(Calendar.YEAR) - 4;
            yearTo = Calendar.getInstance().get(Calendar.YEAR);
            System.out.println(yearFrom);
            System.out.println(yearTo);
        }
        // All time selected
        else if (Objects.equals(selectedOption, yearOptionsList.get(3))) {
            yearFrom = icsAssetVulnerabilityDAO.getMinYearFromDB();
            yearTo = Calendar.getInstance().get(Calendar.YEAR);
            System.out.println(yearFrom);
            System.out.println(yearTo);
        }
    }

    // Root Node: The current evaluation (Display the criticalSystem Name) -> Object Evaluation
    // Immediate children: All assets linked to the evaluation (display will be asset name) -> Object EvaluationAsset
    // Children of immediate children: for each asset, the linked CWEs Object CWE (display will be CWE-ID) -> Object CWE
    // Children of children: for each CWE, get the related attack patterns (display will be the name of attack Pattern and CAPEC-ID) -> Object AttackPattern
    private TreeItem<String> generateAttackTree() {
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

    // Just checking that I'm getting data atm
    @FXML
    private void onNextClick(ActionEvent event) {
        // check if selected option has a value, if not error
        if (selectedOption == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a timeframe to filter by");
            alert.showAndWait();
        }
        // An option is selected, generate attack tree and display it on the tree view scene
        else {
            System.out.println("Critical System Name: " + currentEvaluation.getCriticalSystemName());

            // Generate attack tree and get root node
            TreeItem<String> rootNode = generateAttackTree();

            // save attack tree data to DB, specifically evalID, root node, year from and year to for access later
            evaluationDAO.saveAttackTreeData(evaluationDAO.getLatestEvalID(), currentEvaluation.getCriticalSystemName(), yearFrom, yearTo);


            // Navigate to TreeView Scene and display tree
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("tree-view-scene.fxml"));
                Parent root = loader.load();

                // Get the controller of the tree view scene
                TreeViewSceneController treeViewSceneController = loader.getController();

                // Send data to the tree view controller, we are using this to fetch linked CVEs according to the users date filter.
                treeViewSceneController.getYearFrom(yearFrom);
                treeViewSceneController.getYearTo(yearTo);
                //treeViewSceneController.getAssetType(assetType);
                //treeViewSceneController.getAssetName(assetName);
                treeViewSceneController.getCurrentEvaluation(currentEvaluation);
                treeViewSceneController.getEvaluationAssets(retrievedEvaluationAssets);

                // Pass the root node of the tree to the tree view controller
                // attach context menu
                treeViewSceneController.setRootNode(rootNode, true);

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
    }
}
