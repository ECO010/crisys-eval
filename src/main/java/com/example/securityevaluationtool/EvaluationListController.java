package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.Evaluation;
import com.example.securityevaluationtool.database.EvaluationDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class EvaluationListController {
    public static final String SCENE_TITLE = "List Of Evaluations";
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
    // Delete all assets linked to the evalID 1st, then delete the evaluation as a whole
    @FXML
    private void onDeleteClick() {

    }

    // Not sure how I'm going to do this at the moment
    @FXML
    private void onLoadClick() {

    }
}
