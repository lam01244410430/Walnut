package com.walnut.app.model;

import jakarta.persistence.*;
import java.time.LocalDate; // <--- QUAN TRỌNG: Dùng LocalDate

@Entity
@Table(name = "training_material")
public class TrainingMaterial {

    @Id
    @Column(name = "material_id", length = 20)
    private String materialID;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "upload_date")
    // KHÔNG DÙNG @Temporal NỮA
    private LocalDate uploadDate;   // <--- Kiểu dữ liệu phải là LocalDate

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public TrainingMaterial() {}

    // Constructor cập nhật
    public TrainingMaterial(String materialID, String content, LocalDate uploadDate, Admin admin) {
        this.materialID = materialID;
        this.content = content;
        this.uploadDate = uploadDate;
        this.admin = admin;
    }

    // Getters & Setters
    public String getMaterialID() { return materialID; }
    public void setMaterialID(String materialID) { this.materialID = materialID; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDate getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }
}