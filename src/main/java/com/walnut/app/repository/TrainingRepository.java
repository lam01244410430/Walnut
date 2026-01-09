package com.walnut.app.repository;

import com.walnut.app.model.TrainingMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<TrainingMaterial, String> {
    // Tìm tất cả tài liệu, sắp xếp theo ngày tải lên mới nhất
    List<TrainingMaterial> findAllByOrderByUploadDateDesc();

    TrainingMaterial findTopByOrderByMaterialIDDesc();

    List<TrainingMaterial> findByContentContainingIgnoreCaseOrderByUploadDateDesc(String keyword);
}