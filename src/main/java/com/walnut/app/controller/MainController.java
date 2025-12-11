package com.walnut.app.controller;

import com.walnut.app.model.Admin;
import com.walnut.app.model.Expert;
import com.walnut.app.model.Farmer;
import com.walnut.app.repository.AdminRepository;
import com.walnut.app.repository.ExpertRepository;
import com.walnut.app.repository.FarmerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    @Autowired private FarmerRepository farmerRepo;
    @Autowired private AdminRepository adminRepo;
    @Autowired private ExpertRepository expertRepo;

    // 1. Trang chủ
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // 2. Trang Login
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {

        // 1. KIỂM TRA ADMIN
        Admin admin = adminRepo.findByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            session.setAttribute("user", admin);
            session.setAttribute("role", "ADMIN");
            // SỬA: Không vào dashboard ngay, mà về trang chủ
            return "redirect:/";
        }

        // 2. KIỂM TRA EXPERT
        Expert expert = expertRepo.findByUsername(username);
        if (expert != null && expert.getPassword().equals(password)) {
            session.setAttribute("user", expert);
            session.setAttribute("role", "EXPERT");
            // SỬA: Về trang chủ
            return "redirect:/";
        }

        // 3. KIỂM TRA FARMER
        Farmer farmer = farmerRepo.findByUsername(username);
        if (farmer != null && farmer.getPassword().equals(password)) {
            session.setAttribute("user", farmer);
            session.setAttribute("role", "FARMER");
            // Vẫn về trang chủ như cũ
            return "redirect:/";
        }

        // 4. LỖI ĐĂNG NHẬP
        model.addAttribute("error", "登录失败：用户名或密码错误 (Sai tên đăng nhập hoặc mật khẩu)");
        return "login";
    }

    // Route cho nút '去查询' (Tra cứu sâu bệnh)
    @GetMapping("/search-pest")
    public String searchPestPage() {
        return "search_pest"; // Trả về giao diện tìm kiếm
    }

    // Route cho nút '看数据' (Dự đoán sản lượng)
    @GetMapping("/predict-yield")
    public String predictYieldPage() {
        return "predict_yield"; // Trả về giao diện dự đoán
    }

    // Route cho nút '问专家' (Tư vấn chuyên gia)
    @GetMapping("/consultation")
    public String consultationPage() {
        return "consultation"; // Trả về giao diện tư vấn
    }
}