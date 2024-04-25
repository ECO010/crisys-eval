package com.example.securityevaluationtool.database;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ICSAsset {
    private String assetType;
    private String description;

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private List<ICSAsset> parseAssetDataFromCSV() {

        List<ICSAsset> icsAssetsToSave = new ArrayList<>();

        String csvFilePath = "your file path here";

        try (Reader reader = new FileReader(csvFilePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                ICSAsset icsAsset = new ICSAsset();

                String assetType = csvRecord.get("ICS Asset Type");
                String description = csvRecord.get("Description");

                icsAsset.setAssetType(assetType);
                icsAsset.setDescription(description);

                icsAssetsToSave.add(icsAsset);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return icsAssetsToSave;
    }
    public static void main(String[] args) {
        ICSAssetDAO icsAssetDAO = new ICSAssetDAO();
        ICSAsset icsAsset = new ICSAsset();

        List<ICSAsset> icsAssets = icsAsset.parseAssetDataFromCSV();

        icsAssetDAO.saveIcsAssets(icsAssets);
    }
}
