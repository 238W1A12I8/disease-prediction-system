package com.example.diseaseprediction.controller;

import com.example.diseaseprediction.dto.DiseaseDto;
import com.example.diseaseprediction.dto.StatisticsResponse;
import com.example.diseaseprediction.dto.UserDto;
import com.example.diseaseprediction.model.Disease;
import com.example.diseaseprediction.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> users() {
        return ResponseEntity.ok(adminService.listUsers());
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(adminService.statistics());
    }

    @GetMapping("/diseases")
    public ResponseEntity<List<Disease>> listDiseases() {
        return ResponseEntity.ok(adminService.listDiseases());
    }

    @PostMapping("/diseases")
    public ResponseEntity<Disease> createDisease(@RequestBody @Valid DiseaseDto dto) {
        return ResponseEntity.ok(adminService.saveDisease(dto));
    }

    @PutMapping("/diseases/{id}")
    public ResponseEntity<Disease> updateDisease(@PathVariable Long id, @RequestBody @Valid DiseaseDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(adminService.saveDisease(dto));
    }

    @DeleteMapping("/diseases/{id}")
    public ResponseEntity<Void> deleteDisease(@PathVariable Long id) {
        adminService.deleteDisease(id);
        return ResponseEntity.noContent().build();
    }
}
