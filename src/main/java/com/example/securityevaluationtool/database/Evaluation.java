package com.example.securityevaluationtool.database;

public class Evaluation {
    private String criticalSystemName;
    private String evaluationDate;
    private int evaluationScore;

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

    public int getEvaluationScore() {
        return evaluationScore;
    }

    public void setEvaluationScore(int evaluationScore) {
        this.evaluationScore = evaluationScore;
    }
}
