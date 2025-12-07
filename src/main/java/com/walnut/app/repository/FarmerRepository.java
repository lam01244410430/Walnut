package com.walnut.app.repository;

import com.walnut.app.model.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    // Tìm nông dân để đăng nhập
    Farmer findByUsername(String username);
}