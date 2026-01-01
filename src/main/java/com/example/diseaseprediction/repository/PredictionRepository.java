package com.example.diseaseprediction.repository;

import com.example.diseaseprediction.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByUserId(Long userId);

    List<Prediction> findByUserEmailOrderByCreatedAtDesc(String email);
    
    int countByUserId(Long userId);
}
