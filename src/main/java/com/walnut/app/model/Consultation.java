package com.walnut.app.model;

import jakarta.persistence.*;
import java.time.LocalDate; // 1. Import LocalDate

@Entity
@Table(name = "consultation")
public class Consultation {

    @Id
    @Column(name = "consultation_id", length = 50)
    private String consultationID; // Giữ nguyên String

    @Column(name = "question")
    private String question; // Giữ nguyên tên là question

    @Column(name = "answer")
    private String answer;

    // 2. Thêm trường ngày tạo để hiển thị lên web
    @Column(name = "created_date")
    private LocalDate createdDate;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @ManyToOne
    @JoinColumn(name = "expert_id")
    private Expert expert;

    public Consultation() {}

    // --- GETTERS & SETTERS ---
    public String getConsultationID() { return consultationID; }
    public void setConsultationID(String consultationID) { this.consultationID = consultationID; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    // Getter & Setter cho createdDate
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public Expert getExpert() { return expert; }
    public void setExpert(Expert expert) { this.expert = expert; }

    public String getFarmerName() { return farmer != null ? farmer.getName() : "Unknown"; }
}