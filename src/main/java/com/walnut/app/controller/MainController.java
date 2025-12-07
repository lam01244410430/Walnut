package com.walnut.app.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // Đây là trang chủ chung (/)
    @GetMapping("/")
    public String home(HttpSession session) {
        // 1. Nếu chưa đăng nhập -> Đá về trang login
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // 2. Nếu đã đăng nhập -> Vào trang index.html
        // (Header sẽ tự động hiển thị menu đúng theo vai trò Admin/Farmer/Expert)
        return "index";
    }
}