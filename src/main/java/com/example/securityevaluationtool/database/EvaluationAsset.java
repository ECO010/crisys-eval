package com.example.securityevaluationtool.database;

public class EvaluationAsset {
    private String assetName;
    private String AssetType;
    private int assetSafetyScore;
    private int evaluationID;

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetType() {
        return AssetType;
    }

    public void setAssetType(String assetType) {
        AssetType = assetType;
    }

    public int getAssetSafetyScore() {
        return assetSafetyScore;
    }

    public void setAssetSafetyScore(int assetSafetyScore) {
        this.assetSafetyScore = assetSafetyScore;
    }

    public int getEvaluationID() {
        return evaluationID;
    }

    public void setEvaluationID(int evaluationID) {
        this.evaluationID = evaluationID;
    }
}
