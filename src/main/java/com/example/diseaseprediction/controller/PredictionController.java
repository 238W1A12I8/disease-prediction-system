package com.example.diseaseprediction.controller;

import com.example.diseaseprediction.dto.PredictionRequest;
import com.example.diseaseprediction.dto.PredictionResponse;
import com.example.diseaseprediction.dto.PredictionHistoryResponse;
import com.example.diseaseprediction.model.Symptom;
import com.example.diseaseprediction.repository.SymptomRepository;
import com.example.diseaseprediction.service.PredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PredictionController {

    private final PredictionService predictionService;
    private final SymptomRepository symptomRepository;

    public PredictionController(PredictionService predictionService, SymptomRepository symptomRepository) {
        this.predictionService = predictionService;
        this.symptomRepository = symptomRepository;
    }

    @GetMapping("/symptoms")
    public ResponseEntity<List<Symptom>> getSymptoms() {
        return ResponseEntity.ok(symptomRepository.findAll());
    }

    @PostMapping({"/predict", "/predictions"})
    public ResponseEntity<PredictionResponse> predict(Authentication authentication,
                                                      @RequestBody @Valid PredictionRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(predictionService.predict(email, request));
    }

    @GetMapping("/predictions/me")
    public ResponseEntity<List<PredictionHistoryResponse>> myPredictions(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(predictionService.findCurrentUserHistory(email));
    }
}
