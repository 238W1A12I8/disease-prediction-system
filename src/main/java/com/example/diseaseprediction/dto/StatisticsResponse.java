package com.example.diseaseprediction.dto;

import java.util.List;
import java.util.Map;

public class StatisticsResponse {
    private long totalUsers;
    private long totalPredictions;
    private long totalDiseases;
    private long totalSymptoms;
    private Map<String, Long> diseaseCounts;
    private List<TopDisease> topDiseases;

    public StatisticsResponse(long totalUsers, long totalPredictions, Map<String, Long> diseaseCounts) {
        this.totalUsers = totalUsers;
        this.totalPredictions = totalPredictions;
        this.diseaseCounts = diseaseCounts;
    }
    
    public StatisticsResponse(long totalUsers, long totalPredictions, long totalDiseases, 
                               long totalSymptoms, Map<String, Long> diseaseCounts, List<TopDisease> topDiseases) {
        this.totalUsers = totalUsers;
        this.totalPredictions = totalPredictions;
        this.totalDiseases = totalDiseases;
        this.totalSymptoms = totalSymptoms;
        this.diseaseCounts = diseaseCounts;
        this.topDiseases = topDiseases;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalPredictions() {
        return totalPredictions;
    }
    
    public long getTotalDiseases() {
        return totalDiseases;
    }
    
    public long getTotalSymptoms() {
        return totalSymptoms;
    }

    public Map<String, Long> getDiseaseCounts() {
        return diseaseCounts;
    }
    
    public List<TopDisease> getTopDiseases() {
        return topDiseases;
    }
    
    // Inner class for top diseases
    public static class TopDisease {
        private String name;
        private long count;
        
        public TopDisease(String name, long count) {
            this.name = name;
            this.count = count;
        }
        
        public String getName() {
            return name;
        }
        
        public long getCount() {
            return count;
        }
    }
}
