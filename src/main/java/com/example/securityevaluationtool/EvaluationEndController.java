package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.Evaluation;
import com.example.securityevaluationtool.database.EvaluationAsset;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeView;

import java.util.List;

public class EvaluationEndController {
    public final String SCENE_TITLE = "Evaluation End";

    @FXML
    private ProgressIndicator systemSafetyScoreIndicator;

    @FXML
    private Label evaluationEndHeading;

    // Field(s) and method(s) for getting data from previous controller
    private Evaluation currentEvaluation;
    private List<EvaluationAsset> retrievedEvaluationAssets;
    private TreeView<String> attackTreeView;
    private int yearTo;
    private int yearFrom;
    private double systemSafetyScore;

    public void getYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    public void getYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    public void getCurrentEvaluation(Evaluation currentEvaluation) {
        this.currentEvaluation = currentEvaluation;
    }

    public void getEvaluationAssets(List<EvaluationAsset> retrievedEvaluationAssets) {
        this.retrievedEvaluationAssets = retrievedEvaluationAssets;
    }

    public void getGeneratedTree(TreeView<String> attackTreeView) {
        this.attackTreeView = attackTreeView;
    }

    public void getSystemSafetyScore(double systemSafetyScore) {
        this.systemSafetyScore = systemSafetyScore;
    }

    public void updateProgress(double score) {
        // Ensure the score is within the valid range (0.0 to 1.0)
        double progress = Math.max(0.0, Math.min(1.0, score / 100.0));

        // Set the progress value of the progress indicator
        systemSafetyScoreIndicator.setProgress(progress);
    }

    public void updateHeading() {
        evaluationEndHeading.setText("This is the end of the evaluation for " + currentEvaluation.getCriticalSystemName());
    }
}
