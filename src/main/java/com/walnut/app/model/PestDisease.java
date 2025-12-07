package com.walnut.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pest_disease")
public class PestDisease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;      // Tên bệnh (Tiếng Trung)
    private String symptoms;  // Triệu chứng
    @Column(length = 1000)    // Cho phép mô tả dài
    private String treatment; // Cách chữa

    public PestDisease() {}

    // Constructor tiện lợi để nạp dữ liệu
    public PestDisease(String name, String symptoms, String treatment) {
        this.name = name;
        this.symptoms = symptoms;
        this.treatment = treatment;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; } // Cần setter này
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; } // Cần setter này
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; } // Cần setter này
}