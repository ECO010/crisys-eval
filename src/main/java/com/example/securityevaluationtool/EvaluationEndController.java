package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.Evaluation;
import com.example.securityevaluationtool.database.EvaluationAsset;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EvaluationEndController {
    public final String SCENE_TITLE = "Evaluation End";

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
        evaluationEndHeading.setText("This is the end of the evaluation for " + currentEvaluation.getCriticalSystemName());
    }

    @FXML
    private void onReturnClick(ActionEvent event) {
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
                    //if (stage.getTitle().equals(weaknessNodeCardController.SCENE_TITLE)) {
                        stagesToClose.add(stage);
                    //}
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

    // Get Data from Evaluation, Evaluation Asset and Attack Tree Data tables
    // put them in a csv and save to user's system
    @FXML
    private void onDownloadClick(ActionEvent event) {
        System.out.println("TODO");
    }
}
