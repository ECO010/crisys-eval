package com.example.securityevaluationtool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Optional;

public class LandingScene extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LandingScene.class.getResource("landing-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        LandingSceneController landingSceneController = new LandingSceneController();
        stage.setTitle(landingSceneController.SCENE_TITLE);
        stage.setScene(scene);

        // Get the controller instance
        LandingSceneController controller = fxmlLoader.getController();
        controller.setScene(scene);

        // Set the close request handler
        stage.setOnCloseRequest(this::handleCloseRequest);

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // Clear the DataManager when the application is closed
        DataManager.getInstance().clearAllData();
        super.stop();
    }

    private void handleCloseRequest(WindowEvent event) {
        // Show a confirmation dialog
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirm Exit");
        confirmationDialog.setHeaderText("Are you sure you want to exit the application?");
        confirmationDialog.setContentText("All unsaved data will be lost.");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Clear the open stages and close the application
            DataManager.getInstance().clearOpenStages();
            DataManager.getInstance().clearAllData();
            Platform.exit();
        } else {
            // Cancel the close request
            event.consume();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}