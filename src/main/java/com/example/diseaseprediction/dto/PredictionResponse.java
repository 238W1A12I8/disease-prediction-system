package com.example.diseaseprediction.dto;

import java.time.LocalDateTime;

public class PredictionResponse {
    private String diseaseName;
    private double confidence;
    private String precautions;
    private LocalDateTime timestamp;

    public PredictionResponse(String diseaseName, double confidence, String precautions, LocalDateTime timestamp) {
        this.diseaseName = diseaseName;
        this.confidence = confidence;
        this.precautions = precautions;
        this.timestamp = timestamp;
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
