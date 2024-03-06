package com.example.securityevaluationtool.database;

public class AttackIndicator {
    private String indicator;
    public int capecId;

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setCapecId(int capecId) {
        this.capecId = capecId;
    }

    public int getCapecId() {
        return capecId;
    }
}
