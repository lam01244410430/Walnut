package com.walnut.app.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "consultation")
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT") // Cho phép lưu câu hỏi dài
    private String question;

    @Column(columnDefinition = "TEXT") // Cho phép lưu câu trả lời dài
    private String answer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "farmer_id") // Liên kết với bảng Farmer
    private Farmer farmer;

    // --- Constructors ---

    public Consultation() {
        this.createDate = new Date(); // Tự động lấy ngày hiện tại khi khởi tạo rỗng
    }

    public Consultation(String question, Farmer farmer) {
        this.question = question;
        this.farmer = farmer;
        this.createDate = new Date();
    }

    // --- Getters and Setters (Đầy đủ) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    // ĐÂY LÀ PHƯƠNG THỨC BẠN ĐANG CẦN
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
    }
}