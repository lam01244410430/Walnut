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
public class AuthController {

    @Autowired private FarmerRepository farmerRepo;
    @Autowired private AdminRepository adminRepo;
    @Autowired private ExpertRepository expertRepo;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Nếu đã đăng nhập rồi thì chuyển hướng luôn, không hiện trang login nữa
        if (session.getAttribute("user") != null) {
            String role = (String) session.getAttribute("role");
            if ("ADMIN".equals(role)) return "redirect:/admin/dashboard";
            if ("EXPERT".equals(role)) return "redirect:/expert/dashboard";
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) { // Đã bỏ tham số "role"

        // 1. KIỂM TRA BẢNG ADMIN TRƯỚC (Ưu tiên cao nhất)
        Admin admin = adminRepo.findByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            session.setAttribute("user", admin);
            session.setAttribute("role", "ADMIN");
            // Admin: Chuyển thẳng vào Dashboard quản lý
            return "redirect:/admin/dashboard";
        }

        // 2. KIỂM TRA BẢNG EXPERT (CHUYÊN GIA)
        Expert expert = expertRepo.findByUsername(username);
        if (expert != null && expert.getPassword().equals(password)) {
            session.setAttribute("user", expert);
            session.setAttribute("role", "EXPERT");
            // Chuyên gia: Chuyển thẳng vào Bàn làm việc chuyên gia
            return "redirect:/expert/dashboard";
        }

        // 3. KIỂM TRA BẢNG FARMER (NÔNG DÂN)
        Farmer farmer = farmerRepo.findByUsername(username);
        if (farmer != null && farmer.getPassword().equals(password)) {
            session.setAttribute("user", farmer);
            session.setAttribute("role", "FARMER");
            // Nông dân: Chuyển về Trang chủ để tra cứu
            return "redirect:/";
        }

        // 4. NẾU KHÔNG TÌM THẤY AI
        model.addAttribute("error", "登录失败：用户名或密码错误 (Sai tên đăng nhập hoặc mật khẩu)");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/"; // Đăng xuất xong về trang chủ
    }
}