package com.example.securityevaluationtool.database;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MitreAttackMitigation {
    private String mitigationName;
    private String mitigationDescription;
    private String mitigationId;
    private String objectId;

    public String getMitigationName() {
        return mitigationName;
    }

    public void setMitigationName(String mitigationName) {
        this.mitigationName = mitigationName;
    }

    public String getMitigationDescription() {
        return mitigationDescription;
    }

    public void setMitigationDescription(String mitigationDescription) {
        this.mitigationDescription = mitigationDescription;
    }

    public String getMitigationId() {
        return mitigationId;
    }

    public void setMitigationId(String mitigationId) {
        this.mitigationId = mitigationId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public List<MitreAttackMitigation> parseMitigationInfoFromJson () {

        List<MitreAttackMitigation> attackMitigationsToSave = new ArrayList<>();

        try {
            // Read JSON file
            Path jsonFilePath = Paths.get("C:\\Users\\okonj\\Desktop\\SWANSEA FOLDER\\Dissertation (Project)\\cti-ATT-CK-v14.1\\cti-ATT-CK-v14.1\\ics-attack\\ics-attack.json");
            //Path jsonFilePath = Paths.get("C:\\Users\\okonj\\Desktop\\SWANSEA FOLDER\\Dissertation (Project)\\cti-ATT-CK-v14.1\\cti-ATT-CK-v14.1\\enterprise-attack\\enterprise-attack.json");

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
                if (object.getString("type").equals("course-of-action")) {

                    // New AttackTechnique Object
                    MitreAttackMitigation attackMitigation = new MitreAttackMitigation();

                    // Set ObjectId
                    attackMitigation.setObjectId(object.getString("id"));

                    // Set attackTechniqueName
                    attackMitigation.setMitigationName(object.getString("name"));

                    // Set attackTechniqueDescription
                    attackMitigation.setMitigationDescription(object.getString("description"));

                    // Set attackTechniqueId
                    JSONArray externalReferences = object.getJSONArray("external_references");
                    String externalId = externalReferences.getJSONObject(0).getString("external_id");
                    attackMitigation.setMitigationId(externalId);

                    // Testing Attacks Fetched
                    System.out.println("Mitigation Name: " + attackMitigation.getMitigationName());
                    System.out.println("Mitigation ID: " + attackMitigation.getMitigationId());
                    System.out.println("Description: " + attackMitigation.getMitigationDescription());
                    System.out.println("Object Id: " + attackMitigation.getObjectId());

                    attackMitigationsToSave.add(attackMitigation);
                }
                else {
                    System.out.println("Not a course-of-action record");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attackMitigationsToSave;

    }

    public static void main(String[] args) {
        MitreAttackMitigation mitreAttackMitigations = new MitreAttackMitigation();
        MitreAttackMitigationDAO mitreAttackMitigationDAO = new MitreAttackMitigationDAO();

        List<MitreAttackMitigation> attackMitigations = mitreAttackMitigations.parseMitigationInfoFromJson();

        //mitreAttackMitigationDAO.saveAttackMitigations(attackMitigations);

        mitreAttackMitigationDAO.updateAttackTechniques(attackMitigations);

        System.out.println(attackMitigations.size());
    }
}
