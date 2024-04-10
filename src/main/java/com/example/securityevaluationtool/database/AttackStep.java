package com.example.securityevaluationtool.database;

import java.util.List;

public class AttackStep {
    private String step;
    private String phase;
    private String attackStepDescription;
    public int capecId;
    private List <AttackStepTechnique> attackStepTechniques;

    public void setStep(String step) {
        this.step = step;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void setAttackStepDescription(String attackStepDescription) {
        this.attackStepDescription = attackStepDescription;
    }

    public String getStep() {
        return step;
    }

    public String getPhase() {
        return phase;
    }

    public String getAttackStepDescription() {
        return attackStepDescription;
    }

    public void setCapecId(int capecId) {
        this.capecId = capecId;
    }

    public int getCapecId() {
        return capecId;
    }

    public List<AttackStepTechnique> getAttackStepTechniques() {
        return attackStepTechniques;
    }

    public void setAttackStepTechniques(List<AttackStepTechnique> attackStepTechniques) {
        this.attackStepTechniques = attackStepTechniques;
    }

    @Override
    public String toString() {
        return "Execution Flow Step-"+step + ": " + attackStepDescription;
    }
}
