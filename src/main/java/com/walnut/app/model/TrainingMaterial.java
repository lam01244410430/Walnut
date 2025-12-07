package com.walnut.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "training_material")
public class TrainingMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;   // Tiêu đề
    private String content; // Nội dung bài học

    private LocalDate uploadDate; // Ngày đăng

    @ManyToOne
    @JoinColumn(name = "admin_id") // Ai đăng bài này?
    private User author;

    public TrainingMaterial() {}
    // Getters...
}