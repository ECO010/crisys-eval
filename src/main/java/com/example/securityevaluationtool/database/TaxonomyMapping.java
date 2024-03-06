package com.example.securityevaluationtool.database;

/**
 * Taxonomy Mapping with MITRE ATT&CK
 */
public class TaxonomyMapping {
    private String attackTechniqueName;
    private String attackTechniqueId;
    public int capecId;

    public String getAttackTechniqueName() {
        return attackTechniqueName;
    }

    public void setAttackTechniqueName(String attackTechniqueName) {
        this.attackTechniqueName = attackTechniqueName;
    }

    public String getAttackTechniqueId() {
        return attackTechniqueId;
    }

    public void setAttackTechniqueId(String attackTechniqueId) {
        this.attackTechniqueId = attackTechniqueId;
    }

    public void setCapecId(int capecId) {
        this.capecId = capecId;
    }

    public int getCapecId() {
        return capecId;
    }
}
