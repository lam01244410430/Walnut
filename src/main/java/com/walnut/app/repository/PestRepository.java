package com.walnut.app.repository;

import com.walnut.app.model.PestDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Sửa <PestDisease, String> thành <PestDisease, Long>
public interface PestRepository extends JpaRepository<PestDisease, Long> {

    // Tìm kiếm: Nếu Tên chứa từ khóa HOẶC Triệu chứng chứa từ khóa (Không phân biệt hoa thường)
    // Tương ứng với lệnh SQL: ... WHERE lower(symptoms) LIKE %keyword% OR lower(name) LIKE %keyword%
    List<PestDisease> findBySymptomsContainingIgnoreCaseOrNameContainingIgnoreCase(String symptoms, String name);
}