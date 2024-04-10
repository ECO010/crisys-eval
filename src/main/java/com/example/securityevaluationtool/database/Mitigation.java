package com.example.securityevaluationtool.database;

public class Mitigation {
    private String mitigationDescription;
    public int capecId;

    public void setMitigationDescription(String mitigationDescription) {
        this.mitigationDescription = mitigationDescription;
    }

    public String getMitigationDescription() {
        return mitigationDescription;
    }

    public void setCapecId(int capecId) {
        this.capecId = capecId;
    }

    public int getCapecId() {
        return capecId;
    }

    @Override
    public String toString() {
        return "***Potential Mitigation: " + mitigationDescription.trim();
    }
}
