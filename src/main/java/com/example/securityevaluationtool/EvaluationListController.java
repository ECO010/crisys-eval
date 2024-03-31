package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.Evaluation;
import com.example.securityevaluationtool.database.EvaluationDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class EvaluationListController {
    public final String SCENE_TITLE = "List Of Evaluations";
    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<Evaluation, String> evalDT;

    @FXML
    private TableColumn<Evaluation,Integer> evaluationID;

    @FXML
    private TableColumn<Evaluation, Double> evaluationScore;

    @FXML
    private TableView<Evaluation> evaluationTable;

    @FXML
    private Button loadButton;

    @FXML
    private TableColumn<Evaluation, String> systemName;

    private final EvaluationDAO evaluationDAO = new EvaluationDAO();

    @FXML
    private void initialize() {
        // Configure the columns
        // these should match up with the object properties/fields (so Evaluation in this case)
        systemName.setCellValueFactory(new PropertyValueFactory<>("criticalSystemName"));
        evalDT.setCellValueFactory(new PropertyValueFactory<>("evaluationDate"));
        evaluationScore.setCellValueFactory(new PropertyValueFactory<>("evaluationScore"));
        evaluationID.setCellValueFactory(new PropertyValueFactory<>("evaluationID"));

        // Load evaluations from the database
        List<Evaluation> evaluations = evaluationDAO.getEvaluationsFromDatabase();

        // Add evaluations to the TableView
        evaluationTable.getItems().addAll(evaluations);
    }

    // Make sure a row is selected
    // Delete all assets linked to the selected evalID 1st, then delete the evaluation as a whole
    @FXML
    private void onDeleteClick() {

    }

    @FXML
    private void onBackClick(ActionEvent event) {
        try {
            // Load the FXML file of the landing scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("landing-scene.fxml"));
            Parent root = loader.load();

            // Get the controller of the landing scene
            LandingSceneController landingSceneController = loader.getController();

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

    // Not sure how I'm going to do this at the moment
    // I'm thinking retrieve stored instances of only the tree view and the evaluation end
    // User can then save the PDF or CSV (Excel result)
    @FXML
    private void onLoadClick() {

    }
}
