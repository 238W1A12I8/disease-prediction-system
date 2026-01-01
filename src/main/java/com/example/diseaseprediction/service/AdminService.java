package com.example.diseaseprediction.service;

import com.example.diseaseprediction.dto.DiseaseDto;
import com.example.diseaseprediction.dto.StatisticsResponse;
import com.example.diseaseprediction.dto.UserDto;
import com.example.diseaseprediction.model.Disease;
import com.example.diseaseprediction.model.Prediction;
import com.example.diseaseprediction.model.User;
import com.example.diseaseprediction.repository.DiseaseRepository;
import com.example.diseaseprediction.repository.PredictionRepository;
import com.example.diseaseprediction.repository.SymptomRepository;
import com.example.diseaseprediction.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PredictionRepository predictionRepository;
    private final DiseaseRepository diseaseRepository;
    private final SymptomRepository symptomRepository;

    public AdminService(UserRepository userRepository,
                        PredictionRepository predictionRepository,
                        DiseaseRepository diseaseRepository,
                        SymptomRepository symptomRepository) {
        this.userRepository = userRepository;
        this.predictionRepository = predictionRepository;
        this.diseaseRepository = diseaseRepository;
        this.symptomRepository = symptomRepository;
    }

    public List<UserDto> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole().name()))
                .toList();
    }

    public StatisticsResponse statistics() {
        long totalUsers = userRepository.count();
        long totalDiseases = diseaseRepository.count();
        long totalSymptoms = symptomRepository.count();
        List<Prediction> predictions = predictionRepository.findAll();
        long totalPredictions = predictions.size();
        
        Map<String, Long> diseaseCounts = predictions.stream()
                .collect(Collectors.groupingBy(p -> p.getDisease().getDiseaseName(), Collectors.counting()));
        
        // Get top 5 diseases
        List<StatisticsResponse.TopDisease> topDiseases = diseaseCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(e -> new StatisticsResponse.TopDisease(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        
        // If no predictions yet, show some default data
        if (topDiseases.isEmpty()) {
            topDiseases.add(new StatisticsResponse.TopDisease("No predictions yet", 0));
        }
        
        return new StatisticsResponse(totalUsers, totalPredictions, totalDiseases, totalSymptoms, diseaseCounts, topDiseases);
    }

    @Transactional
    public Disease saveDisease(DiseaseDto dto) {
        Disease disease = dto.getId() != null ?
                diseaseRepository.findById(dto.getId()).orElse(new Disease()) : new Disease();
        disease.setDiseaseName(dto.getDiseaseName());
        disease.setDescription(dto.getDescription());
        disease.setPrecautions(dto.getPrecautions());
        return diseaseRepository.save(disease);
    }

    public List<Disease> listDiseases() {
        return diseaseRepository.findAll();
    }

    public void deleteDisease(Long id) {
        diseaseRepository.deleteById(id);
    }
}
