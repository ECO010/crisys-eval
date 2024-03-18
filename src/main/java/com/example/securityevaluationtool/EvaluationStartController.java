package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.Evaluation;
import com.example.securityevaluationtool.database.EvaluationDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EvaluationStartController {

    public final String SCENE_TITLE = "Begin Evaluation";

    private Evaluation evaluation = new Evaluation();
    private EvaluationDAO evaluationDAO = new EvaluationDAO();

    @FXML
    private TextField systemNameField;

    // Check text field is not null or empty, if it is alert and error pop up so the user puts in text
    // If not confirm that the name is what the user wants as you can't get back to this window once you leave (not ideal, but will work with this because of time)
    @FXML
    public void onContinueClick(ActionEvent event) {
        // Get the text from the text field
        String enteredText = systemNameField.getText().trim();

        // Check if the text is null or empty
        if (enteredText.isEmpty()) {
            // Show an alert if the text field is empty
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Name field cannot be empty. Please enter a name for the system you are evaluating.");
            alert.showAndWait();
        }

        // There is an input, ask for confirmation
        else {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm System Name");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Is '" + enteredText + "' the desired input?");

            // Add OK and Cancel buttons to the confirmation dialog
            confirmation.getButtonTypes().clear();
            confirmation.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Show the confirmation dialog and wait for user input
            Optional<ButtonType> result = confirmation.showAndWait();

            // Check the user's choice, User confirmed
            if (result.isPresent() && result.get() == ButtonType.OK) {

                // Get the current local date and time
                LocalDateTime currentDateTime = LocalDateTime.now();

                // Format the date and time as a string
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = currentDateTime.format(formatter);

                // Add Data to Evaluation table
                evaluation.setCriticalSystemName(enteredText);
                evaluation.setEvaluationDate(formattedDateTime);

                evaluationDAO.saveEvaluation(evaluation);


                // Navigate to the Asset Declaration Screen
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("asset-declaration.fxml"));
                    Parent root = loader.load();

                    AssetDeclarationController assetDeclarationController = loader.getController();

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle(assetDeclarationController.SCENE_TITLE);
                    stage.show();

                    // Close the current scene if needed
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    currentStage.close(); // Close instead of hide
                    //((Node)(event.getSource())).getScene().getWindow().hide();
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle loading error
                }
            }
            // User canceled, allow them to enter text again
            else {
                systemNameField.clear();
                systemNameField.requestFocus();
            }
        }
    }

    // cLOSE WINDOW AND GO BACK TO MAIN MENU
    @FXML
    private void onBackClick(ActionEvent event) {
        try {
            // Load the FXML file of the landing scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("landing-scene.fxml"));
            Parent root = loader.load();

            // Get the controller of the landing scene
            LandingSceneController landingSceneController = loader.getController();

            // Set the landing scene controller as the previous controller
            // This allows for communication between scenes if needed
            // landingSceneController.setPreviousController(this);

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
}
