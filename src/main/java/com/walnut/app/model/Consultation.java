package com.walnut.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "consultation")
public class Consultation {

    @Id
    @Column(name = "consultation_id", length = 20) // Quan trọng: Khớp với SQL
    private String consultationID;

    private String question;
    private String answer;

    // Liên kết Farmer
    @ManyToOne
    @JoinColumn(name = "farmer_id") // Khớp với SQL
    private Farmer farmer;

    // Liên kết Expert
    @ManyToOne
    @JoinColumn(name = "expert_id") // Khớp với SQL
    private Expert expert;

    // --- CONSTRUCTORS ---
    public Consultation() {}

    public Consultation(String consultationID, String question, Farmer farmer, Expert expert) {
        this.consultationID = consultationID;
        this.question = question;
        this.farmer = farmer;
        this.expert = expert;
    }

    // --- GETTERS & SETTERS ---
    public String getConsultationID() { return consultationID; }
    public void setConsultationID(String consultationID) { this.consultationID = consultationID; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public Expert getExpert() { return expert; }
    public void setExpert(Expert expert) { this.expert = expert; }

    // Helper để lấy tên hiển thị an toàn
    public String getFarmerName() { return farmer != null ? farmer.getName() : "Unknown"; }
}