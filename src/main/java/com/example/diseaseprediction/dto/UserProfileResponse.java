package com.example.diseaseprediction.dto;

public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private int totalPredictions;
    private String memberSince;

    public UserProfileResponse(Long id, String name, String email, String role, int totalPredictions, String memberSince) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.totalPredictions = totalPredictions;
        this.memberSince = memberSince;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public int getTotalPredictions() { return totalPredictions; }
    public String getMemberSince() { return memberSince; }
}
