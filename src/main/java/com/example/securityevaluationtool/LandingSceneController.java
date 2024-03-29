package com.example.securityevaluationtool;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LandingSceneController {

    public final String SCENE_TITLE = "CriSysEval: A Security Evaluation Tool For Critical Systems";

    // Navigate to the evaluation start scene
    @FXML
    protected void onBeginEvaluationClick(ActionEvent event) {
        // Navigate to the Tree Prompt Screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evaluation-start.fxml")); // change the file to test
            Parent root = loader.load();

            EvaluationStartController evaluationStartController = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(evaluationStartController.SCENE_TITLE);
            stage.show();

            // Close the current scene
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
    }

    // Navigate to the Evaluation List scene
    @FXML
    protected void onPastEvaluationClick(ActionEvent event) {
        // Navigate to the Tree Prompt Screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evaluation-list-scene.fxml"));
            Parent root = loader.load();

            EvaluationListController evaluationListController = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(evaluationListController.SCENE_TITLE);
            stage.show();

            // Close the current scene
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
    }

    // Navigate to the scene for ICS vulnerabilities
    @FXML
    protected void onCheckIcsAssetClick(ActionEvent event) {
        // Navigate to the Tree Prompt Screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("icsa-database-view.fxml"));
            Parent root = loader.load();

            IcsaDatabaseViewController icsaDatabaseViewController = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(icsaDatabaseViewController.SCENE_TITLE);
            stage.show();

            // Close the current scene
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
    }

    @FXML
    private ImageView myImage;

    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/operating.png")));
        myImage.setImage(image);
    }
}