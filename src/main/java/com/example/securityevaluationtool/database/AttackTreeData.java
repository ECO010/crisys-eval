package com.example.securityevaluationtool.database;

import java.util.List;

public class AttackTreeData {
    private int evaluationID;
    private String root;
    private int yearFrom;
    private int yearTo;
    private List<EvaluationAsset> assets;
    private List<CommonWeaknessEnumeration> linkedCWEs;
    private List<AttackPattern> linkedCAPECs;
    private List<String> linkedCVES;
    private List<String> combinedMitigations;

    public int getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(int evaluationID) {
        this.evaluationID = evaluationID;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public int getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    public int getYearTo() {
        return yearTo;
    }

    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }
}
