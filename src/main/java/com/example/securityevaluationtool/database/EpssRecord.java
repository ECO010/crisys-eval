package com.example.securityevaluationtool.database;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class EpssRecord {
    private String cveNumber;
    private double epssScore;
    private double percentile;

    public String getCveNumber() {
        return cveNumber;
    }

    public void setCveNumber(String cveNumber) {
        this.cveNumber = cveNumber;
    }

    public double getEpssScore() {
        return epssScore;
    }

    public void setEpssScore(double epssScore) {
        this.epssScore = epssScore;
    }

    public double getPercentile() {
        return percentile;
    }

    public void setPercentile(double percentile) {
        this.percentile = percentile;
    }

    private List<EpssRecord> parseEpssDataFromCSV() {

        List<EpssRecord> epssRecordsToSave = new ArrayList<>();

        String csvFilePath = "C:\\Users\\okonj\\Desktop\\SWANSEA FOLDER\\Dissertation (Project)\\EPSS\\epss_scores-2024-03-26.csv";

        try (Reader reader = new FileReader(csvFilePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                EpssRecord epssRecord = new EpssRecord();

                String cveNumber = csvRecord.get("cve");
                String epssScore = csvRecord.get("epss");
                String percentile = csvRecord.get("percentile");

                epssRecord.setCveNumber(cveNumber);
                epssRecord.setEpssScore(100 * Double.parseDouble(epssScore));
                epssRecord.setPercentile(100 * Double.parseDouble(percentile));

                epssRecordsToSave.add(epssRecord);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return epssRecordsToSave;
    }

    public static void main(String[] args) {
        EpssRecordDAO epssRecordDAO = new EpssRecordDAO();
        EpssRecord epssRecord = new EpssRecord();
        List<EpssRecord> epssRecordsToSave = epssRecord.parseEpssDataFromCSV();
        epssRecordDAO.saveEpssRecords(epssRecordsToSave);
    }
}
