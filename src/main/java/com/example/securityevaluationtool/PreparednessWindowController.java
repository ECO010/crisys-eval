package com.example.securityevaluationtool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreparednessWindowController {
    public final String SCENE_TITLE = "Preparedness Survey";

    @FXML
    private Button backToTreeViewBtn;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Button continueBtn;

    @FXML
    private void initialize() {
        List<String> options = new ArrayList<>();
        options.add("Extremely Prepared");
        options.add("Somewhat Prepared");
        options.add("Neither Prepared Nor Unprepared");
        options.add("Somewhat Unprepared");
        options.add("Extremely Unprepared");
        ObservableList<String> observableOptions = FXCollections.observableList(options);
        comboBox.setItems(observableOptions);
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
        // Close the current scene (preparedness window)
        Stage stage = (Stage) continueBtn.getScene().getWindow();
        stage.close();

        // Close Tree view
        closeTreeViewScene();

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
