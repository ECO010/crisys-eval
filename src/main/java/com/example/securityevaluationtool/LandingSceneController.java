package com.example.securityevaluationtool;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LandingSceneController {

    // TODO:
    //  Implement ICS Asset vulnerability functionality:
    //  Get Asset data into DB, Get Vulnerability data into DB, Display them on another scene
    //  Filter them by Release Date, Asset Type, Vendor, CVSS Scores
    //  Get rid of useless stuff
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onBeginEvaluationClick(ActionEvent event) {
        // Navigate to the Tree Prompt Screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tree-prompt-scene.fxml"));
            Parent root = loader.load();

          /*  TreePromptSceneController controller = loader.getController();
            controller.setPreviousController(this);*/

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tree Prompt Scene");
            stage.show();

            // Close the current scene if needed
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error
        }
    }

    @FXML
    protected void onCheckIcsAssetClick() {
        System.out.println("TODO");
    }

    @FXML
    private ImageView myImage;

    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/operating.png")));
        myImage.setImage(image);
    }
}