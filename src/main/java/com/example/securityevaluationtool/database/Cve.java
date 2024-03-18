package com.example.securityevaluationtool.database;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Cve {
    private String cveNum;
    private String cveTitle;
    private String cweId;
    private String description;

    public String getCveNum() {
        return cveNum;
    }

    public void setCveNum(String cveNum) {
        this.cveNum = cveNum;
    }

    public String getCveTitle() {
        return cveTitle;
    }

    public void setCveTitle(String cveTitle) {
        this.cveTitle = cveTitle;
    }

    public String getCweId() {
        return cweId;
    }

    public void setCweId(String cweId) {
        this.cweId = cweId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Cve> parseCveFiles(String directoryPath) {

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        List<Cve> cvesToSave = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                // If the current file is a directory, recursively call the method
                if (file.isDirectory()) {
                    // Append the CVEs returned by the recursive call to the list
                    cvesToSave.addAll(parseCveFiles(file.getAbsolutePath()));
                }
                // If the current file is a JSON file, read its content
                else if (file.getName().toLowerCase().endsWith(".json")) {
                    try {
                        String jsonContent = new String(Files.readAllBytes(Paths.get(file.getPath())));

                        Cve cve = new Cve();

                        // Parse JSON content into JSONObject or JSONArray
                        JSONObject jsonObject = new JSONObject(jsonContent);

                        // Extract the required fields
                        String cveId = jsonObject.getJSONObject("cveMetadata").getString("cveId");
                        String title = jsonObject.getJSONObject("containers")
                                .getJSONObject("cna")
                                .getString("title");

                        String cweId = jsonObject.getJSONObject("containers")
                                .getJSONArray("problemTypes")
                                .getJSONObject(0)
                                .getJSONArray("descriptions")
                                .getJSONObject(0)
                                .getString("cweId");

                        String descriptionValue = jsonObject.getJSONObject("containers")
                                .getJSONArray("descriptions")
                                .getJSONObject(0)
                                .getString("value");

                        cve.setCveNum(cveId);
                        cve.setCveTitle(title);
                        cve.setDescription(descriptionValue);
                        cve.setCweId(cweId != null ? cweId : "");

                        cvesToSave.add(cve);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return cvesToSave;
    }

    public static void main(String[] args) {
        // Main directory containing subdirectories with JSON files
        String mainDirectory = "C:\\Users\\okonj\\Desktop\\SWANSEA FOLDER\\Dissertation (Project)\\CVEs";

        Cve cve = new Cve();
        CveDAO cveDAO = new CveDAO();

        List<Cve> cvesToSave = cve.parseCveFiles(mainDirectory);
        cveDAO.saveCVEs(cvesToSave);
    }
}
