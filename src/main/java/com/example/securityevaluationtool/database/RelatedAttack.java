package com.example.securityevaluationtool.database;

public class RelatedAttack {
    private String nature;
    public int relationFromCapecId;
    private int relationToCapecId;

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public int getRelationToCapecId() {
        return relationToCapecId;
    }

    public void setRelationToCapecId(int relationToCapecId) {
        this.relationToCapecId = relationToCapecId;
    }

    public void setRelationFromCapecId(int relationFromCapecId) {
        this.relationFromCapecId = relationFromCapecId;
    }

    public int getRelationFromCapecId() {
        return relationFromCapecId;
    }
}
