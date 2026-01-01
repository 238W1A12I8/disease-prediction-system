package com.example.diseaseprediction.controller;

import com.example.diseaseprediction.dto.UpdateProfileRequest;
import com.example.diseaseprediction.dto.UserProfileResponse;
import com.example.diseaseprediction.model.User;
import com.example.diseaseprediction.repository.PredictionRepository;
import com.example.diseaseprediction.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/users")
@Tag(name = "User Profile", description = "User profile management endpoints")
public class UserController {

    private final UserRepository userRepository;
    private final PredictionRepository predictionRepository;

    public UserController(UserRepository userRepository, PredictionRepository predictionRepository) {
        this.userRepository = userRepository;
        this.predictionRepository = predictionRepository;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the authenticated user's profile information")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int predictionCount = predictionRepository.countByUserId(user.getId());
        
        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                predictionCount,
                "Member since 2025"
        ));
    }

    @PutMapping("/me")
    @Operation(summary = "Update user profile", description = "Updates the authenticated user's profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @RequestBody @Valid UpdateProfileRequest request) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(request.getName());
        userRepository.save(user);

        int predictionCount = predictionRepository.countByUserId(user.getId());

        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                predictionCount,
                "Member since 2025"
        ));
    }
}
