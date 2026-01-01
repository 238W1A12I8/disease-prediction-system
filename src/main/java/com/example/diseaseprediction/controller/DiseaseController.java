package com.example.diseaseprediction.controller;

import com.example.diseaseprediction.model.Disease;
import com.example.diseaseprediction.repository.DiseaseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diseases")
@Tag(name = "Diseases", description = "Public disease information endpoints")
public class DiseaseController {

    private final DiseaseRepository diseaseRepository;

    public DiseaseController(DiseaseRepository diseaseRepository) {
        this.diseaseRepository = diseaseRepository;
    }

    @GetMapping
    @Operation(summary = "Get all diseases", description = "Returns a list of all diseases in the system")
    public ResponseEntity<List<Disease>> getAllDiseases() {
        return ResponseEntity.ok(diseaseRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get disease by ID", description = "Returns detailed information about a specific disease")
    public ResponseEntity<Disease> getDiseaseById(@PathVariable Long id) {
        return diseaseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search diseases", description = "Search diseases by name")
    public ResponseEntity<List<Disease>> searchDiseases(@RequestParam String query) {
        List<Disease> results = diseaseRepository.findAll().stream()
                .filter(d -> d.getDiseaseName().toLowerCase().contains(query.toLowerCase()))
                .toList();
        return ResponseEntity.ok(results);
    }
}
