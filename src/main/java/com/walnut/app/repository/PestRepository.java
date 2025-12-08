package com.walnut.app.repository;

import com.walnut.app.model.PestDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PestRepository extends JpaRepository<PestDisease, String> {

    // Khai báo hàm này để Spring Data JPA tự động tạo câu lệnh SQL tìm kiếm
    // Tìm theo Symptoms (Triệu chứng) HOẶC Name (Tên bệnh), không phân biệt hoa thường
    List<PestDisease> findBySymptomsContainingIgnoreCaseOrNameContainingIgnoreCase(String symptoms, String name);
}