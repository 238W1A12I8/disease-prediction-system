package com.example.diseaseprediction.repository;

import com.example.diseaseprediction.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    Optional<Disease> findByDiseaseName(String diseaseName);
}
