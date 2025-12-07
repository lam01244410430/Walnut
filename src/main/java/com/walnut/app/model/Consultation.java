package com.walnut.app.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "consultation")
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question; // Câu hỏi
    private String answer;   // Câu trả lời (ban đầu sẽ null)
    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    public Consultation() {}
    public Consultation(String question, Farmer farmer) {
        this.question = question;
        this.farmer = farmer;
        this.createDate = new Date();
    }
    // Getters Setters...
}