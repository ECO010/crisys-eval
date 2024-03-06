package com.example.securityevaluationtool.database;

import java.util.List;

public class Consequence {
    private List<String> impacts;
    private List<String> scopes;
    public int capecId;

    public void setCapecId(int capecId) {
        this.capecId = capecId;
    }

    public int getCapecId() {
        return capecId;
    }

    public List<String> getImpacts() {
        return impacts;
    }

    public void setImpacts(List<String> impact) {
        this.impacts = impact;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scope) {
        this.scopes = scope;
    }
}
