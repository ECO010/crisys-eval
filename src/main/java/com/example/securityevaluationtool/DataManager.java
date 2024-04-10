package com.example.securityevaluationtool;

import com.example.securityevaluationtool.database.Evaluation;
import com.example.securityevaluationtool.database.EvaluationAsset;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Stage> openStages;
    private List<EvaluationAsset> evaluationAssets;
    private Evaluation currentEvaluation;
    private int attackTreeYearFrom;

    private int attackTreeYearTo;

    private DataManager() {
        openStages = new ArrayList<>();
        evaluationAssets = new ArrayList<>();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public List<Stage> getOpenStages() {
        return openStages;
    }

    public void addOpenStage(Stage stage) {
        openStages.add(stage);
    }

    public void removeOpenStage(Stage stage) {
        openStages.remove(stage);
    }

    public List<EvaluationAsset> getEvaluationAssets() {
        return evaluationAssets;
    }

    public void setEvaluationAssets(List<EvaluationAsset> assets) {
        evaluationAssets = assets;
    }

    public Evaluation getCurrentEvaluation() {
        return currentEvaluation;
    }

    public void setCurrentEvaluation(Evaluation evaluation) {
        currentEvaluation = evaluation;
    }

    public int getAttackTreeYearFrom() {
        return attackTreeYearFrom;
    }

    public void setAttackTreeYearFrom(int attackTreeYearFrom) {
        this.attackTreeYearFrom = attackTreeYearFrom;
    }

    public int getAttackTreeYearTo() {
        return attackTreeYearTo;
    }

    public void setAttackTreeYearTo(int attackTreeYearTo) {
        this.attackTreeYearTo = attackTreeYearTo;
    }

    public void clearOpenStages() {
        openStages.clear();
    }

    public void clearAllData() {
        evaluationAssets.clear();
        currentEvaluation = null;
    }
}