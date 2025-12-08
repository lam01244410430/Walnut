package com.walnut.app.repository;

import com.walnut.app.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {
    // Tìm admin để đăng nhập
    Admin findByUsername(String username);
}