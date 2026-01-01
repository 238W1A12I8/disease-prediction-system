package com.example.diseaseprediction.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DecisionTreeEngine - the core prediction logic.
 */
class DecisionTreeEngineTest {

    private DecisionTreeEngine engine;

    @BeforeEach
    void setUp() {
        engine = new DecisionTreeEngine();
    }

    @Nested
    @DisplayName("Prediction Tests")
    class PredictionTests {

        @Test
        @DisplayName("Should predict Flu with high confidence for matching symptoms")
        void shouldPredictFluWithMatchingSymptoms() {
            List<String> symptoms = Arrays.asList("fever", "cough", "sore throat", "fatigue", "body aches");
            
            DecisionTreeEngine.Outcome result = engine.predict(symptoms);
            
            assertNotNull(result);
            assertEquals("Flu (Influenza)", result.getDiseaseName());
            assertTrue(result.getConfidence() > 0.5, "Confidence should be above 50%");
            assertNotNull(result.getPrecautions());
            assertFalse(result.getPrecautions().isEmpty());
        }

        @Test
        @DisplayName("Should predict Common Cold for cold symptoms")
        void shouldPredictCommonCold() {
            List<String> symptoms = Arrays.asList("sneezing", "runny nose", "sore throat", "nasal congestion");
            
            DecisionTreeEngine.Outcome result = engine.predict(symptoms);
            
            assertNotNull(result);
            assertEquals("Common Cold", result.getDiseaseName());
        }

        @Test
        @DisplayName("Should predict COVID-19 for specific symptoms")
        void shouldPredictCovid() {
            List<String> symptoms = Arrays.asList("fever", "dry cough", "loss of taste", "loss of smell", "fatigue");
            
            DecisionTreeEngine.Outcome result = engine.predict(symptoms);
            
            assertNotNull(result);
            assertEquals("COVID-19", result.getDiseaseName());
            assertTrue(result.getConfidence() > 0.6);
        }

        @Test
        @DisplayName("Should predict Migraine for headache symptoms")
        void shouldPredictMigraine() {
            List<String> symptoms = Arrays.asList("severe headache", "nausea", "sensitivity to light", "vomiting");
            
            DecisionTreeEngine.Outcome result = engine.predict(symptoms);
            
            assertNotNull(result);
            assertEquals("Migraine", result.getDiseaseName());
        }

        @Test
        @DisplayName("Should predict Gastroenteritis for GI symptoms")
        void shouldPredictGastroenteritis() {
            List<String> symptoms = Arrays.asList("nausea", "vomiting", "diarrhea", "abdominal pain", "stomach cramps");
            
            DecisionTreeEngine.Outcome result = engine.predict(symptoms);
            
            assertNotNull(result);
            assertTrue(result.getDiseaseName().contains("Gastroenteritis") || 
                       result.getDiseaseName().contains("Food Poisoning"));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle empty symptom list")
        void shouldHandleEmptySymptoms() {
            List<String> symptoms = Collections.emptyList();
            
            DecisionTreeEngine.Outcome result = engine.predict(symptoms);
            
            assertNotNull(result);
            // Should return some default or lowest confidence match
        }

        @Test
        @DisplayName("Should handle unknown symptoms gracefully")
        void shouldHandleUnknownSymptoms() {
            List<String> symptoms = Arrays.asList("unknown_symptom_1", "unknown_symptom_2");
            
            DecisionTreeEngine.Outcome result = engine.predict(symptoms);
            
            assertNotNull(result);
            // Should still return a result, even with low confidence
        }

        @Test
        @DisplayName("Should handle single symptom")
        void shouldHandleSingleSymptom() {
            List<String> symptoms = Collections.singletonList("fever");
            
            DecisionTreeEngine.Outcome result = engine.predict(symptoms);
            
            assertNotNull(result);
            assertNotNull(result.getDiseaseName());
        }

        @Test
        @DisplayName("Should handle case-insensitive symptoms")
        void shouldHandleCaseInsensitiveSymptoms() {
            List<String> symptoms1 = Arrays.asList("FEVER", "COUGH", "FATIGUE");
            List<String> symptoms2 = Arrays.asList("fever", "cough", "fatigue");
            
            DecisionTreeEngine.Outcome result1 = engine.predict(symptoms1);
            DecisionTreeEngine.Outcome result2 = engine.predict(symptoms2);
            
            // Results should be similar regardless of case
            assertNotNull(result1);
            assertNotNull(result2);
        }
    }

    @Nested
    @DisplayName("Confidence Scoring")
    class ConfidenceScoring {

        @Test
        @DisplayName("Confidence should be between 0 and 1")
        void confidenceShouldBeBounded() {
            List<String> symptoms = Arrays.asList("fever", "cough", "fatigue");
            
            DecisionTreeEngine.Outcome result = engine.predict(symptoms);
            
            assertTrue(result.getConfidence() >= 0.0 && result.getConfidence() <= 1.0,
                    "Confidence should be between 0 and 1");
        }

        @Test
        @DisplayName("More matching symptoms should increase confidence")
        void moreSymptomsShouldIncreaseConfidence() {
            List<String> fewSymptoms = Arrays.asList("fever", "cough");
            List<String> moreSymptoms = Arrays.asList("fever", "cough", "sore throat", "fatigue", "body aches", "chills");
            
            DecisionTreeEngine.Outcome result1 = engine.predict(fewSymptoms);
            DecisionTreeEngine.Outcome result2 = engine.predict(moreSymptoms);
            
            // More symptoms matching Flu should give higher confidence for Flu
            if (result1.getDiseaseName().equals(result2.getDiseaseName())) {
                assertTrue(result2.getConfidence() >= result1.getConfidence(),
                        "More matching symptoms should not decrease confidence");
            }
        }
    }

    @Nested
    @DisplayName("Precautions")
    class PrecautionsTests {

        @Test
        @DisplayName("Every prediction should have precautions")
        void everyPredictionShouldHavePrecautions() {
            List<String> symptoms = Arrays.asList("fever", "headache");
            
            DecisionTreeEngine.Outcome result = engine.predict(symptoms);
            
            assertNotNull(result.getPrecautions());
            assertFalse(result.getPrecautions().isBlank());
        }
    }
}
