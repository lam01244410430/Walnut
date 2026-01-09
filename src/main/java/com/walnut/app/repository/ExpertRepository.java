package com.walnut.app.repository;

import com.walnut.app.model.Expert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, String> {
    // 根据用户名查询专家 (Tìm chuyên gia theo tên đăng nhập)
    Expert findByUsername(String username);
    Expert findTopByOrderByExpertIDDesc();
}