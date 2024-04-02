package com.example.securityevaluationtool.database;

public class AttackTreeData {
    private int evaluationID;
    private String root;
    private int yearFrom;
    private int yearTo;

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
