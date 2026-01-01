package com.example.diseaseprediction.dto;

import java.time.LocalDateTime;

public class PredictionHistoryResponse {
    private final Long predictionId;
    private final String diseaseName;
    private final double confidence;
    private final String precautions;
    private final LocalDateTime timestamp;

    public PredictionHistoryResponse(Long predictionId, String diseaseName, double confidence, String precautions, LocalDateTime timestamp) {
        this.predictionId = predictionId;
        this.diseaseName = diseaseName;
        this.confidence = confidence;
        this.precautions = precautions;
        this.timestamp = timestamp;
    }

    public Long getPredictionId() {
        return predictionId;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getPrecautions() {
        return precautions;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
