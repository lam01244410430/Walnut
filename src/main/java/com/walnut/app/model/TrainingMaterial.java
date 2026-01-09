package com.walnut.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "training_material")
public class TrainingMaterial {

    @Id
    @Column(name = "material_id", length = 20)
    private String materialID;

    @Column(columnDefinition = "TEXT")
    private String content; // Lưu trữ: "Tiêu đề (File: abc.pdf)"

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public TrainingMaterial() {}

    public TrainingMaterial(String materialID, String content, LocalDate uploadDate, Admin admin) {
        this.materialID = materialID;
        this.content = content;
        this.uploadDate = uploadDate;
        this.admin = admin;
    }

    // --- GETTERS & SETTERS (Giữ nguyên) ---
    public String getMaterialID() { return materialID; }
    public void setMaterialID(String materialID) { this.materialID = materialID; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDate getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; }
    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    // --- THÊM 2 HÀM HỖ TRỢ NÀY ---

    // 1. Lấy tên file thực tế: "abc.pdf"
    public String getRealFileName() {
        if (content != null && content.contains("(File: ") && content.endsWith(")")) {
            try {
                int startIndex = content.lastIndexOf("(File: ") + 7;
                int endIndex = content.length() - 1;
                return content.substring(startIndex, endIndex);
            } catch (Exception e) { return ""; }
        }
        return "";
    }

    // 2. Lấy tiêu đề sạch (để hiển thị)
    public String getCleanTitle() {
        if (content != null && content.contains("(File: ")) {
            try {
                return content.substring(0, content.lastIndexOf("(File: ")).trim();
            } catch (Exception e) { return content; }
        }
        return content;
    }
}