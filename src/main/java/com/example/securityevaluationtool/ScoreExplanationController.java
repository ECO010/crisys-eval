package com.example.securityevaluationtool;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class ScoreExplanationController {
    public final String SCENE_TITLE = "Score Breakdown";

    // Bring the open tree view window to the front and into focus
    @FXML
    private void onTreeViewHyperlinkClick(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tree-view-scene.fxml"));
            Parent root = loader.load();

            // Get the controller of the Score Breakdown
            TreeViewSceneController treeViewSceneController = loader.getController();

            // Check if the Score Explanation window is already open
            Stage treeViewStage = getWindowByTitle(treeViewSceneController.SCENE_TITLE);
            if (treeViewStage == null) {
                // If not open, create a new stage
                treeViewStage = new Stage();
                treeViewStage.setScene(new Scene(root));
                treeViewStage.setTitle(treeViewSceneController.SCENE_TITLE);
            }
            else {
                // If already open, just bring it to the front
                treeViewStage.toFront();
            }

            // Close the current scene
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

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
}
