package com.example.diseaseprediction.model;

import jakarta.persistence.*;

@Entity
@Table(name = "symptoms")
public class Symptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String symptomName;

    public Symptom() {
    }

    public Symptom(String symptomName) {
        this.symptomName = symptomName;
    }

    public Long getId() {
        return id;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }
}
