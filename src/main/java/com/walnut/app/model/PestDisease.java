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
    private String treatment; // Cách chữa

    public PestDisease() {}
    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSymptoms() { return symptoms; }
    public String getTreatment() { return treatment; }
}