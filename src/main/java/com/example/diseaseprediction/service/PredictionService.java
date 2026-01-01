package com.example.diseaseprediction.service;

import com.example.diseaseprediction.dto.PredictionRequest;
import com.example.diseaseprediction.dto.PredictionResponse;
import com.example.diseaseprediction.dto.PredictionHistoryResponse;
import com.example.diseaseprediction.model.Disease;
import com.example.diseaseprediction.model.Prediction;
import com.example.diseaseprediction.model.Symptom;
import com.example.diseaseprediction.model.User;
import com.example.diseaseprediction.repository.DiseaseRepository;
import com.example.diseaseprediction.repository.PredictionRepository;
import com.example.diseaseprediction.repository.SymptomRepository;
import com.example.diseaseprediction.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class PredictionService {

    private final UserRepository userRepository;
    private final DiseaseRepository diseaseRepository;
    private final PredictionRepository predictionRepository;
    private final SymptomRepository symptomRepository;
    private final DecisionTreeEngine decisionTreeEngine;

    public PredictionService(UserRepository userRepository,
                             DiseaseRepository diseaseRepository,
                             PredictionRepository predictionRepository,
                             SymptomRepository symptomRepository,
                             DecisionTreeEngine decisionTreeEngine) {
        this.userRepository = userRepository;
        this.diseaseRepository = diseaseRepository;
        this.predictionRepository = predictionRepository;
        this.symptomRepository = symptomRepository;
        this.decisionTreeEngine = decisionTreeEngine;
    }

    @Transactional
    public PredictionResponse predict(String userEmail, PredictionRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DecisionTreeEngine.Outcome outcome = decisionTreeEngine.predict(request.getSymptoms());
        Disease disease = diseaseRepository.findByDiseaseName(outcome.getDiseaseName())
                .orElseGet(() -> diseaseRepository.save(new Disease(outcome.getDiseaseName(),
                        "Auto-generated from rule set", outcome.getPrecautions())));

        // Persist symptoms to the catalog if new
        request.getSymptoms().forEach(symptomName -> {
            String normalized = symptomName.toLowerCase(Locale.ROOT);
            symptomRepository.findBySymptomName(normalized)
                    .orElseGet(() -> symptomRepository.save(new Symptom(normalized)));
        });

        Prediction prediction = new Prediction(user, disease, outcome.getConfidence());
        predictionRepository.save(prediction);

        return new PredictionResponse(disease.getDiseaseName(), outcome.getConfidence(),
                disease.getPrecautions(), prediction.getCreatedAt());
    }

    public List<Prediction> findUserPredictions(Long userId) {
        return predictionRepository.findByUserId(userId);
    }

    public List<PredictionHistoryResponse> findCurrentUserHistory(String email) {
        return predictionRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(p -> new PredictionHistoryResponse(
                        p.getId(),
                        p.getDisease().getDiseaseName(),
                        p.getConfidence(),
                        p.getDisease().getPrecautions(),
                        p.getCreatedAt()))
                .toList();
    }
}
