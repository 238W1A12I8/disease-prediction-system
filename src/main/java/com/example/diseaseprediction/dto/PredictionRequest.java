package com.example.diseaseprediction.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PredictionRequest {

    @NotEmpty(message = "Please select at least one symptom")
    private List<String> symptoms;

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }
}
