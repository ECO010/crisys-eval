package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.Evaluation;
import com.example.securityevaluationtool.database.EvaluationAsset;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class ScoreExplanationController {
    public final String SCENE_TITLE = "Score Breakdown";
    private final List<EvaluationAsset> retrievedEvaluationAssets = DataManager.getInstance().getEvaluationAssets();
    private final Evaluation currentEvaluation = DataManager.getInstance().getCurrentEvaluation();
    private final int yearFrom = DataManager.getInstance().getAttackTreeYearFrom();
    private final int yearTo = DataManager.getInstance().getAttackTreeYearTo();

    // Bring the open tree view window to the front and into focus
    @FXML
    private void onTreeViewHyperlinkClick(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tree-view-scene.fxml"));
            Parent root = loader.load();

            // Get the open stages from the DataManager
            List<Stage> openStages = DataManager.getInstance().getOpenStages();

            // Get the controller of the Score Breakdown
            TreeViewSceneController treeViewSceneController = loader.getController();
            EvaluationListController evaluationListController = new EvaluationListController();

            // Check if the Score Explanation window is already open
            Stage treeViewStage = getStageByTitle(openStages, treeViewSceneController.SCENE_TITLE);
            if (treeViewStage == null) {
                TreeItem<String> rootNode = evaluationListController.generateAttackTree(currentEvaluation, retrievedEvaluationAssets, yearFrom, yearTo);
                treeViewSceneController.setRootNode(rootNode, false);

                // If not open, create a new stage
                treeViewStage = new Stage();
                treeViewStage.setScene(new Scene(root));
                treeViewStage.setTitle(treeViewSceneController.SCENE_TITLE);
                treeViewStage.show();
            }
            else {
                if (!treeViewStage.isShowing()) {
                    treeViewStage.show();
                }
                // If already open, just bring it to the front
                treeViewStage.toFront();
            }

            // Close the current scene
            //Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            //currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
    }

    // Bring the open eval end window to the front and into focus
    @FXML
    private void onEvalOverviewHyperlinkClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evaluation-end.fxml"));
            Parent root = loader.load();

            // Get the controller of the Score Breakdown
            EvaluationEndController evaluationEndController = loader.getController();

            // Check if the Score Explanation window is already open
            Stage evaluationOverviewStage = getWindowByTitle(evaluationEndController.SCENE_TITLE);
            if (evaluationOverviewStage == null) {
                // If not open, create a new stage
                evaluationOverviewStage = new Stage();
                evaluationOverviewStage.setScene(new Scene(root));
                evaluationOverviewStage.setTitle(evaluationEndController.SCENE_TITLE);
            }
            else {
                // If already open, just bring it to the front
                evaluationOverviewStage.toFront();
            }

            // Close the current scene
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
    }

    // Helper method to get the window by its title
    private Stage getWindowByTitle(String title) {
        // Iterate through all open stages
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                // Check if the stage is the preparedness window
                if (stage.getTitle().equals(title)) {
                    return stage;
                }
            }
        }
        return null;
    }

    private Stage getStageByTitle(List<Stage> stages, String title) {
        return stages.stream()
                .filter(stage -> stage.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }
}
