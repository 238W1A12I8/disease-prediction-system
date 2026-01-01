package com.example.diseaseprediction.controller;

import com.example.diseaseprediction.dto.LoginRequest;
import com.example.diseaseprediction.dto.PredictionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PredictionController endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PredictionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        LoginRequest userLogin = new LoginRequest();
        userLogin.setEmail("user@demo.com");
        userLogin.setPassword("password");

        MvcResult userResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andReturn();
        userToken = objectMapper.readTree(userResult.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Nested
    @DisplayName("POST /api/predict")
    class PredictEndpoint {

        @Test
        @DisplayName("Should return prediction for valid symptoms")
        void shouldReturnPrediction() throws Exception {
            PredictionRequest request = new PredictionRequest();
            request.setSymptoms(Arrays.asList("fever", "cough", "fatigue"));

            mockMvc.perform(post("/predict")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.diseaseName", notNullValue()))
                    .andExpect(jsonPath("$.confidence", notNullValue()))
                    .andExpect(jsonPath("$.precautions", notNullValue()));
        }

        @Test
        @DisplayName("Should require authentication")
        void shouldRequireAuthentication() throws Exception {
            PredictionRequest request = new PredictionRequest();
            request.setSymptoms(Arrays.asList("fever", "cough"));

            mockMvc.perform(post("/predict")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/predictions/history")
    class HistoryEndpoint {

        @Test
        @DisplayName("Should return user's prediction history")
        void shouldReturnPredictionHistory() throws Exception {
            mockMvc.perform(get("/predictions/me")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", isA(java.util.List.class)));
        }
    }
}
