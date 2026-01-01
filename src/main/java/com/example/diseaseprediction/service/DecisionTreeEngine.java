package com.example.diseaseprediction.service;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Simple, explainable decision-tree-like scorer that matches symptom overlap.
 */
@Component
public class DecisionTreeEngine {

    public static class Outcome {
        private final String diseaseName;
        private final double confidence;
        private final String precautions;

        public Outcome(String diseaseName, double confidence, String precautions) {
            this.diseaseName = diseaseName;
            this.confidence = confidence;
            this.precautions = precautions;
        }

        public String getDiseaseName() {
            return diseaseName;
        }

        public double getConfidence() {
            return confidence;
        }

        public String getPrecautions() {
            return precautions;
        }
    }

    private final Map<String, List<String>> symptomTree;
    private final Map<String, String> precautions;

    public DecisionTreeEngine() {
        symptomTree = new LinkedHashMap<>();
        precautions = new HashMap<>();
        seedRules();
    }

    private void seedRules() {
        // Respiratory Diseases
        symptomTree.put("Flu (Influenza)", List.of("fever", "high fever", "cough", "sore throat", "fatigue", "body aches", "chills"));
        precautions.put("Flu (Influenza)", "Rest, drink fluids, take antiviral medications if prescribed. Avoid contact with others.");

        symptomTree.put("Common Cold", List.of("sneezing", "runny nose", "sore throat", "nasal congestion", "mild fever"));
        precautions.put("Common Cold", "Rest, stay hydrated, use over-the-counter cold medications.");

        symptomTree.put("COVID-19", List.of("fever", "dry cough", "loss of taste", "loss of smell", "fatigue", "breathing difficulty", "body aches"));
        precautions.put("COVID-19", "Isolate immediately, wear a mask, consult healthcare provider. Monitor oxygen levels.");

        symptomTree.put("Pneumonia", List.of("high fever", "productive cough", "chest pain", "shortness of breath", "fatigue", "chills"));
        precautions.put("Pneumonia", "Seek medical attention immediately. May require antibiotics or hospitalization.");

        symptomTree.put("Bronchitis", List.of("cough", "productive cough", "chest tightness", "fatigue", "mild fever", "sore throat"));
        precautions.put("Bronchitis", "Rest, drink fluids, use humidifier. Avoid smoking and air pollutants.");

        symptomTree.put("Asthma", List.of("wheezing", "shortness of breath", "chest tightness", "cough", "breathing difficulty"));
        precautions.put("Asthma", "Use prescribed inhalers, avoid triggers, have an action plan ready.");

        symptomTree.put("Tuberculosis", List.of("cough", "productive cough", "night sweats", "weight loss", "fatigue", "fever", "chest pain"));
        precautions.put("Tuberculosis", "Requires long-term antibiotic treatment. Highly contagious - isolate and seek medical care.");

        // Neurological Conditions
        symptomTree.put("Migraine", List.of("severe headache", "throbbing headache", "nausea", "sensitivity to light", "sensitivity to sound", "vomiting"));
        precautions.put("Migraine", "Rest in a dark, quiet room. Take prescribed medications. Stay hydrated.");

        symptomTree.put("Tension Headache", List.of("headache", "fatigue", "muscle pain", "difficulty sleeping"));
        precautions.put("Tension Headache", "Over-the-counter pain relievers, stress management, adequate sleep.");

        symptomTree.put("Vertigo", List.of("dizziness", "vertigo", "nausea", "vomiting", "hearing loss", "ringing in ears"));
        precautions.put("Vertigo", "Sit or lie down immediately. Avoid sudden movements. See ENT specialist.");

        // Gastrointestinal
        symptomTree.put("Gastroenteritis", List.of("nausea", "vomiting", "diarrhea", "abdominal pain", "fever", "stomach cramps"));
        precautions.put("Gastroenteritis", "Stay hydrated, eat bland foods, rest. Seek care if symptoms persist over 48 hours.");

        symptomTree.put("Food Poisoning", List.of("nausea", "vomiting", "diarrhea", "abdominal pain", "fever", "weakness"));
        precautions.put("Food Poisoning", "Stay hydrated, rest. Seek medical care if severe vomiting or bloody stools.");

        symptomTree.put("Acid Reflux (GERD)", List.of("heartburn", "chest pain", "difficulty swallowing", "nausea", "bloating"));
        precautions.put("Acid Reflux (GERD)", "Avoid trigger foods, eat smaller meals, don't lie down after eating.");

        // Infectious Diseases
        symptomTree.put("Malaria", List.of("high fever", "chills", "sweating", "headache", "nausea", "vomiting", "body aches"));
        precautions.put("Malaria", "Seek immediate medical treatment. Antimalarial drugs required.");

        symptomTree.put("Dengue Fever", List.of("high fever", "severe headache", "joint pain", "muscle pain", "rash", "fatigue", "nausea"));
        precautions.put("Dengue Fever", "No specific treatment. Rest, hydrate, take pain relievers (avoid aspirin).");

        symptomTree.put("Typhoid", List.of("high fever", "headache", "abdominal pain", "weakness", "loss of appetite", "diarrhea", "constipation"));
        precautions.put("Typhoid", "Antibiotics required. Hospitalization may be necessary for severe cases.");

        symptomTree.put("Chickenpox", List.of("rash", "blisters", "fever", "fatigue", "itchy skin", "headache", "loss of appetite"));
        precautions.put("Chickenpox", "Calamine lotion for itching, antihistamines, stay hydrated. Isolate from others.");

        // Allergies & Skin
        symptomTree.put("Allergic Rhinitis", List.of("sneezing", "runny nose", "nasal congestion", "itchy eyes", "watery eyes"));
        precautions.put("Allergic Rhinitis", "Antihistamines, nasal sprays, avoid allergens.");

        symptomTree.put("Conjunctivitis (Pink Eye)", List.of("red eyes", "itchy eyes", "watery eyes", "eye discharge", "swelling"));
        precautions.put("Conjunctivitis (Pink Eye)", "Warm compresses, eye drops. Bacterial cases need antibiotic drops.");

        symptomTree.put("Sinusitis", List.of("nasal congestion", "sinus pressure", "headache", "runny nose", "cough", "fatigue"));
        precautions.put("Sinusitis", "Nasal decongestants, saline rinses, rest. Antibiotics if bacterial.");

        // Other
        symptomTree.put("Tonsillitis", List.of("sore throat", "difficulty swallowing", "fever", "swollen lymph nodes", "headache"));
        precautions.put("Tonsillitis", "Rest, warm liquids, pain relievers. May need antibiotics if bacterial.");

        symptomTree.put("Ear Infection (Otitis Media)", List.of("ear pain", "fever", "hearing loss", "headache", "irritability"));
        precautions.put("Ear Infection (Otitis Media)", "Pain relievers, warm compresses. Antibiotics may be prescribed.");

        symptomTree.put("Diabetes (Type 2)", List.of("frequent urination", "excessive thirst", "fatigue", "weight loss", "blurred vision"));
        precautions.put("Diabetes (Type 2)", "Diet management, regular exercise, blood sugar monitoring, medications.");

        symptomTree.put("Anemia", List.of("fatigue", "weakness", "pale skin", "shortness of breath", "dizziness", "rapid heartbeat"));
        precautions.put("Anemia", "Iron supplements, dietary changes, treat underlying cause.");

        symptomTree.put("Hypertension", List.of("headache", "shortness of breath", "dizziness", "chest pain", "fatigue"));
        precautions.put("Hypertension", "Lifestyle changes, reduced salt intake, regular exercise, medications if prescribed.");
    }

    public Outcome predict(List<String> symptoms) {
        if (symptoms == null || symptoms.isEmpty()) {
            return new Outcome("Unknown", 0.0, "Please provide symptoms.");
        }

        double bestScore = -1.0;
        String bestDisease = "Unknown";
        String bestPrecaution = "General precautions: rest, hydrate, seek professional advice.";

        for (Map.Entry<String, List<String>> entry : symptomTree.entrySet()) {
            String disease = entry.getKey();
            List<String> ruleSymptoms = entry.getValue();
            long matches = symptoms.stream()
                    .map(String::toLowerCase)
                    .filter(ruleSymptoms.stream().map(String::toLowerCase).collect(java.util.stream.Collectors.toSet())::contains)
                    .count();
            double score = (double) matches / ruleSymptoms.size();
            if (score > bestScore) {
                bestScore = score;
                bestDisease = disease;
                bestPrecaution = precautions.getOrDefault(disease, bestPrecaution);
            }
        }

        double confidence = Math.max(0.05, Math.min(0.99, bestScore));
        return new Outcome(bestDisease, confidence, bestPrecaution);
    }
}
