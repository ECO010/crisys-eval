package com.example.securityevaluationtool.database;

public class Evaluation {
    private String criticalSystemName;
    private String evaluationDate;
    private double evaluationScore;

    public String getCriticalSystemName() {
        return criticalSystemName;
    }

    public void setCriticalSystemName(String criticalSystemName) {
        this.criticalSystemName = criticalSystemName;
    }

    public String getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(String evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public double getEvaluationScore() {
        return evaluationScore;
    }

    public void setEvaluationScore(double evaluationScore) {
        this.evaluationScore = evaluationScore;
    }
}
