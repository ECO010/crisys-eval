package com.example.securityevaluationtool;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller class for the landing scene
 * ICS Operations image design attributed to Eucalyp
 * URL: https://www.flaticon.com/free-icons/system" title="system icons
 */
public class LandingSceneController {

    public final String SCENE_TITLE = "CriSysEval: A Security Evaluation Tool For Critical Systems";

    @FXML
    private AnchorPane anchorPane;

    private Scene scene;

    @FXML
    private Button beginNewButton;

    @FXML
    private HBox buttonHBox;

    @FXML
    private Button checkCisaDbButton;

    @FXML
    private ImageView myImage;

    @FXML
    private Label sloganLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Button viewPastButton;

    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/operating.png")));
        myImage.setImage(image);
        attachLayoutListeners();
    }

    public void initializeLayoutListeners() {
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            updateLayout((double) newValue, scene.getHeight());
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            updateLayout(scene.getWidth(), (double) newValue);
        });
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        initializeLayoutListeners();
        attachLayoutListeners();
    }

    public void attachLayoutListeners() {
        if (scene != null) {
            scene.widthProperty().addListener((observable, oldValue, newValue) -> {
                updateLayout((double) newValue, scene.getHeight());
            });
            scene.heightProperty().addListener((observable, oldValue, newValue) -> {
                updateLayout(scene.getWidth(), (double) newValue);
            });
        }
    }

    private void updateLayout(double newWidth, double newHeight) {
        // Update the layout of the elements based on the new window size
        // For example, you can resize and reposition the elements using their
        // prefWidth, prefHeight, layoutX, and layoutY properties
        anchorPane.setPrefWidth(newWidth);
        anchorPane.setPrefHeight(newHeight);
        // Adjust the layout of other elements in the scene
        // Update the layout of the elements based on the new window size
        titleLabel.setLayoutX((newWidth - titleLabel.getPrefWidth()) / 2);
        sloganLabel.setLayoutX((newWidth - sloganLabel.getPrefWidth()) / 2);
        myImage.setLayoutX((newWidth - myImage.getFitWidth()) / 2);
        buttonHBox.setLayoutX((newWidth - buttonHBox.getPrefWidth()) / 2);

        // Adjust the size of the elements
        titleLabel.setPrefWidth(newWidth * 0.5); // 50% of the window width
        sloganLabel.setPrefWidth(newWidth * 0.5); // 50% of the window width
        myImage.setFitWidth(newWidth * 0.5); // 50% of the window width
        myImage.setFitHeight(newHeight * 0.6); // 60% of the window height
        buttonHBox.setPrefWidth(newWidth * 0.8); // 80% of the window width

        // Adjust the spacing and padding of the HBox
        buttonHBox.setSpacing(newWidth * 0.02); // 2% of the window width
        buttonHBox.setPadding(new Insets(newHeight * 0.05, 0, 0, 0)); // 5% of the window height
    }

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evaluation-list-scene.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();

            Scene scene = new Scene(root);

            // Set the scene for the EvaluationListController
            stage.setScene(scene);

            EvaluationListController evaluationListController = loader.getController();

            stage.setTitle(evaluationListController.SCENE_TITLE);

            // Show the stage after setting the scene
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
}