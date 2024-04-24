package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.AttackTreeData;
import com.example.securityevaluationtool.database.Evaluation;
import com.example.securityevaluationtool.database.EvaluationAsset;
import com.example.securityevaluationtool.database.EvaluationDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EvaluationEndController {
    public final String SCENE_TITLE = "Evaluation End";

    private final EvaluationDAO evaluationDAO = new EvaluationDAO();

    @FXML
    private ProgressIndicator systemSafetyScoreIndicator;

    @FXML
    private Label evaluationEndHeading;

    // Field(s) and method(s) for getting data from previous controller
    private Evaluation currentEvaluation;
    private List<EvaluationAsset> retrievedEvaluationAssets;
    private TreeView<String> attackTreeView;
    private int yearTo;
    private int yearFrom;
    private double systemSafetyScore;

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

    public void getSystemSafetyScore(double systemSafetyScore) {
        this.systemSafetyScore = systemSafetyScore;
    }

    public void updateProgress(double score) {
        // Ensure the score is within the valid range (0.0 to 1.0)
        double progress = Math.max(0.0, Math.min(1.0, score / 100.0));

        // Set the progress value of the progress indicator
        systemSafetyScoreIndicator.setProgress(progress);
    }

    public void updateHeading() {
        evaluationEndHeading.setText("This is the evaluation result for: " + currentEvaluation.getCriticalSystemName());
    }

    // Open up new window which explains how the score was calculated
    // List the category point ratings
    // Detail the score for each asset in the evaluation
    // Don't want it to close any window it's doing that at the moment (that's because I would like to navigate to those same windows by clicking a hyperlink_)
    @FXML
    private void onHyperlinkClick() {
        // Navigate to Score Breakdown Scene/Window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("score-explanation.fxml"));
            Parent root = loader.load();

            // Get the controller of the Score Breakdown
            ScoreExplanationController scoreExplanationController = loader.getController();

            // Create a new stage for the Score Explanation window
            Stage scoreExplanationStage = new Stage();
            scoreExplanationStage.setScene(new Scene(root));
            scoreExplanationStage.setTitle(scoreExplanationController.SCENE_TITLE);

            scoreExplanationStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
    }

    @FXML
    private void onReturnClick(ActionEvent event) {
        DataManager.getInstance().clearOpenStages();
        try {
            // Load the FXML file of the landing scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("landing-scene.fxml"));
            Parent root = loader.load();

            // Get the controller of the landing scene
            LandingSceneController landingSceneController = loader.getController();
            TreeViewSceneController treeViewSceneController = new TreeViewSceneController();

            // Get a list of all open windows
            List<Window> openWindows = Window.getWindows();

            // List of Card windows
            List<Stage> stagesToClose = new ArrayList<>();
            // Iterate through the open windows and close all open windows
            for (Window window : openWindows) {
                if (window instanceof Stage) {
                    Stage stage = (Stage) window;
                    stagesToClose.add(stage);
                }
            }
            treeViewSceneController.closeAllWindows(stagesToClose);
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

    // Get Data from Evaluation and Evaluation Asset tables as Strings
    // put them in a csv and save to user's system
    @FXML
    private void onDownloadClick(ActionEvent event) {
        // Get the evaluation ID
        int evaluationID = currentEvaluation.getEvaluationID();

        // Prompt the user to select a directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save CSV");
        File selectedDirectory = directoryChooser.showDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedDirectory != null) {
            // Prepare the file path for the CSV file
            String filePath = selectedDirectory.getAbsolutePath() + "/Evaluation Data For "+currentEvaluation.getCriticalSystemName()+".csv";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                // Get Eval data from DB
                Evaluation retrievedEval = evaluationDAO.retrieveEvaluationData(evaluationID);
                retrievedEvaluationAssets = evaluationDAO.retrieveEvaluationAssetData(evaluationID);
                AttackTreeData retrievedAttackTreeData = evaluationDAO.retrieveAttackTreeData(evaluationID);

                // Convert data into List of strings that I can then loop through
                List<String> evaluationData = new ArrayList<>();
                List<String> evaluationAssetData = new ArrayList<>();
                List<String> attackTreeData = new ArrayList<>();

                // Add evaluation data to the list
                evaluationData.add("Evaluation ID: " + retrievedEval.getEvaluationID());
                evaluationData.add("System Name: " + retrievedEval.getCriticalSystemName());
                evaluationData.add("Evaluation Score: " + retrievedEval.getEvaluationScore());
                evaluationData.add("Evaluation Date: " + retrievedEval.getEvaluationDate());

                // Add asset data to the list
                for (EvaluationAsset asset : retrievedEvaluationAssets) {
                    evaluationAssetData.add("Asset Name: " + asset.getAssetName());
                    evaluationAssetData.add("Asset Type: " + asset.getAssetType());
                    evaluationAssetData.add("Asset Safety Score: " + asset.getAssetSafetyScore());
                }

                // Add attack tree data to the list
                attackTreeData.add("Attack Tree Root (System Name): " + retrievedAttackTreeData.getRoot());
                attackTreeData.add("YearFrom: " + retrievedAttackTreeData.getYearFrom());
                attackTreeData.add("YearTo: " + retrievedAttackTreeData.getYearTo());

                // Write data to the CSV file starting with Evaluation table data
                writer.write("*** Evaluation Data ***\n");
                for (String data : evaluationData) {
                    writer.write(data + "\n");
                }
                // Write Evaluation Asset table data
                writer.write("\n\n*** Evaluation Asset Data ***\n");
                for (String data : evaluationAssetData) {
                    writer.write(data + "\n");
                }
               // Write Attack tree table data
                writer.write("\n\n*** Attack Tree Data ***\n");
                for (String data : attackTreeData) {
                    writer.write(data + "\n");
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Save Successful");
                alert.setHeaderText(null);
                alert.setContentText("Evaluation data saved to: " + filePath);
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No directory selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a directory to save the file to");
            alert.showAndWait();
        }
    }
}
