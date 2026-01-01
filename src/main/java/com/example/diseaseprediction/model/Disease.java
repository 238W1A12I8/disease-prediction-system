package com.example.diseaseprediction.model;

import jakarta.persistence.*;

@Entity
@Table(name = "diseases")
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String diseaseName;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String precautions;

    public Disease() {
    }

    public Disease(String diseaseName, String description, String precautions) {
        this.diseaseName = diseaseName;
        this.description = description;
        this.precautions = precautions;
    }

    public Long getId() {
        return id;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrecautions() {
        return precautions;
    }

    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }
}
