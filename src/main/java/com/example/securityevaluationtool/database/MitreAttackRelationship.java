package com.example.securityevaluationtool.database;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MitreAttackRelationship {
    private String mitigationObjectId;
    private String techniqueObjectId;
    private String action;

    public String getMitigationObjectId() {
        return mitigationObjectId;
    }

    public void setMitigationObjectId(String mitigationObjectId) {
        this.mitigationObjectId = mitigationObjectId;
    }

    public String getTechniqueObjectId() {
        return techniqueObjectId;
    }

    public void setTechniqueObjectId(String techniqueObjectId) {
        this.techniqueObjectId = techniqueObjectId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<MitreAttackRelationship> parseRelationshipInfoFromJson() {

        List<MitreAttackRelationship> attackRelationshipsToSave = new ArrayList<>();

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
                if (object.getString("type").equals("relationship")) {

                    // New AttackTechnique Object
                    MitreAttackRelationship attackRelationship = new MitreAttackRelationship();

                    // Set MitigationObjectId
                    attackRelationship.setMitigationObjectId(object.getString("source_ref"));

                    // Set TechniqueObjectId
                    attackRelationship.setTechniqueObjectId(object.getString("target_ref"));

                    // Set relationship action
                    attackRelationship.setAction(object.getString("relationship_type"));

                    // Testing Attacks Fetched
                    if (attackRelationship.getAction().equalsIgnoreCase("mitigates")) {
                        System.out.println("Mitigation ObjectID: " + attackRelationship.getMitigationObjectId());
                        System.out.println("Technique ObjectID: " + attackRelationship.getTechniqueObjectId());
                        System.out.println("Action: " + attackRelationship.getAction());
                        attackRelationshipsToSave.add(attackRelationship);
                    }
                }
                else {
                    System.out.println("Not a relationship record");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attackRelationshipsToSave;
    }

    public static void main(String[] args) {
        MitreAttackRelationship mitreAttackRelationship = new MitreAttackRelationship();
        MitreAttackRelationshipDAO mitreAttackRelationshipDAO = new MitreAttackRelationshipDAO();

        List<MitreAttackRelationship> mitreAttackRelationships = mitreAttackRelationship.parseRelationshipInfoFromJson();

        mitreAttackRelationshipDAO.saveAttackRelationships(mitreAttackRelationships);

        //mitreAttackRelationshipDAO.(attackMitigations);

        System.out.println(mitreAttackRelationships.size());
    }
}
