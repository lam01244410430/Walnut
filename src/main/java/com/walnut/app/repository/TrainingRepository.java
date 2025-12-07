package com.walnut.app.repository;

import com.walnut.app.model.TrainingMaterial; // Import đúng Entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<TrainingMaterial, Long> {

    // 1. Tìm kiếm tài liệu theo tiêu đề (Hỗ trợ Search)
    List<TrainingMaterial> findByTitleContaining(String keyword);

    // 2. Lọc theo loại file (Ví dụ: Chỉ lấy video hướng dẫn)
    List<TrainingMaterial> findByFileType(String fileType);

    // 3. Lấy danh sách mới nhất
    List<TrainingMaterial> findAllByOrderByUploadDateDesc();
}