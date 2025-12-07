package com.walnut.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "training_materials") // Tên bảng trong Database
public class TrainingMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;        // 资料标题 (Tiêu đề tài liệu)

    @Column(length = 1000)
    private String description;  // 描述 (Mô tả chi tiết)

    @Column(nullable = false)
    private String filePath;     // 文件路径 (Đường dẫn file lưu trên server)

    private String fileType;     // 文件类型 (Loại file: PDF, MP4, JPG...)

    private Long fileSize;       // 文件大小 (Kích thước file - tính bằng byte)

    private LocalDateTime uploadDate; // 上传时间 (Thời gian đăng)

    // --- Constructors ---

    public TrainingMaterial() {
        this.uploadDate = LocalDateTime.now(); // Tự động lấy giờ hiện tại khi tạo
    }

    public TrainingMaterial(String title, String description, String filePath, String fileType, Long fileSize) {
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadDate = LocalDateTime.now();
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
}