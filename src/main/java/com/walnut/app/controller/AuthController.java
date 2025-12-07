package com.walnut.app.controller;

import com.walnut.app.model.Admin;
import com.walnut.app.model.Farmer;
import com.walnut.app.repository.AdminRepository;
import com.walnut.app.repository.FarmerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired private FarmerRepository farmerRepo;
    @Autowired private AdminRepository adminRepo;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String role, // Thêm lựa chọn vai trò
                              HttpSession session,
                              Model model) {

        if ("FARMER".equals(role)) {
            Farmer farmer = farmerRepo.findByUsername(username);
            if (farmer != null && farmer.getPassword().equals(password)) {
                session.setAttribute("user", farmer);
                session.setAttribute("role", "FARMER");
                return "redirect:/"; // Vào trang chủ nông dân
            }
        } else if ("ADMIN".equals(role)) {
            Admin admin = adminRepo.findByUsername(username);
            if (admin != null && admin.getPassword().equals(password)) {
                session.setAttribute("user", admin);
                session.setAttribute("role", "ADMIN");
                return "redirect:/admin/upload"; // Vào trang upload tài liệu (cần tạo sau)
            }
        }

        model.addAttribute("error", "用户名或密码错误 (Sai thông tin đăng nhập)");
        return "login";
    }
}