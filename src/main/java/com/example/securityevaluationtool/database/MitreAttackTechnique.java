package com.example.securityevaluationtool.database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class MitreAttackTechnique {

    private String attackTechniqueName;
    private String attackTechniqueDescription;
    private String attackTechniqueId;
    private String objectId;
    private List<KillChainPhase> killChainPhases;

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

    public List<KillChainPhase> getKillChainPhases() {
        return killChainPhases;
    }

    public void setKillChainPhases(List<KillChainPhase> killChainPhases) {
        this.killChainPhases = killChainPhases;
    }

    public String getAttackTechniqueDescription() {
        return attackTechniqueDescription;
    }

    public void setAttackTechniqueDescription(String attackTechniqueDescription) {
        this.attackTechniqueDescription = attackTechniqueDescription;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public List<MitreAttackTechnique> parseJsonAttackTechniqueFile() {

        List<MitreAttackTechnique> attackTechniquesToSave = new ArrayList<>();

        try {
            // Read JSON file
            Path jsonFilePath = Paths.get("your file path here");

            // Read the content of the file
            List<String> lines = Files.readAllLines(jsonFilePath);
            StringBuilder stringBuilder = new StringBuilder();
            for (String line : lines) {
                stringBuilder.append(line);
            }

            // Parse the JSON string and make it the root
            JSONObject root = new JSONObject(stringBuilder.toString());

            JSONArray objects = root.getJSONArray("objects");

            // Extract the desired information from the objects array
            for (int i = 0; i < objects.length(); i++) {
                JSONObject object = objects.getJSONObject(i);
                if (object.getString("type").equals("attack-pattern")) {

                    // New AttackTechnique Object
                    MitreAttackTechnique attackTechnique = new MitreAttackTechnique();

                    // Set ObjectId
                    attackTechnique.setObjectId(object.getString("id"));

                    // Set attackTechniqueName
                    attackTechnique.setAttackTechniqueName(object.getString("name"));

                    // Set attackTechniqueDescription
                    attackTechnique.setAttackTechniqueDescription(object.getString("description"));

                    // Set attackTechniqueId
                    JSONArray externalReferences = object.getJSONArray("external_references");
                    String externalId = externalReferences.getJSONObject(0).getString("external_id");
                    attackTechnique.setAttackTechniqueId(externalId);

                    // Set killChainPhases
                    List<KillChainPhase> killChainPhases = new ArrayList<>();
                    JSONArray killChainPhasesArray = object.getJSONArray("kill_chain_phases");
                    for (int k = 0; k < killChainPhasesArray.length(); k++) {
                        JSONObject phaseNode = killChainPhasesArray.getJSONObject(k);
                        KillChainPhase killChainPhase = new KillChainPhase();
                        killChainPhase.setKillChainPhase(phaseNode.getString("phase_name"));

                        // Set the attack technique Id for each kill chain phase
                        killChainPhase.setAttackTechniqueId(attackTechnique.getAttackTechniqueId());

                        killChainPhases.add(killChainPhase);

                        // Testing kill chains fetched
                        System.out.println(killChainPhase.getKillChainPhase());
                    }
                    attackTechnique.setKillChainPhases(killChainPhases);

                    // Testing Attacks Fetched
                    System.out.println("Attack Technique Name: " + attackTechnique.getAttackTechniqueName());
                    System.out.println("Attack Technique ID: " + attackTechnique.getAttackTechniqueId());
                    System.out.println("Kill Chain Phases: " + attackTechnique.getKillChainPhases());
                    System.out.println("ObjectId: " + attackTechnique.getObjectId());

                    attackTechniquesToSave.add(attackTechnique);

                }
                else {
                    System.out.println("Not an attack-pattern record");
                }
            }
            //System.out.println(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attackTechniquesToSave;
    }

    /**
     * Main method just to add stuff to the db will delete once satisfied with the db
     *
     * @param args
     */
    public static void main(String[] args) {
        MitreAttackTechnique mitreAttackTechnique = new MitreAttackTechnique();
        MitreAttackTechniqueDAO mitreAttackTechniqueDAO = new MitreAttackTechniqueDAO();
        //KillChainPhaseDAO killChainPhaseDAO = new KillChainPhaseDAO();

        List<MitreAttackTechnique> mitreAttackTechniques = mitreAttackTechnique.parseJsonAttackTechniqueFile();

        // Already added
        //mitreAttackTechniqueDAO.saveAttackTechniques(mitreAttackTechniques);
        mitreAttackTechniqueDAO.updateAttackTechniques(mitreAttackTechniques);

        // add each kill chain phase for a particular attack technique id to the db
        /*for (MitreAttackTechnique attackTechnique : mitreAttackTechniques) {
            killChainPhaseDAO.saveKillChainPhases(attackTechnique.getKillChainPhases());
        }*/

        System.out.println(mitreAttackTechniques.size());
    }
}
