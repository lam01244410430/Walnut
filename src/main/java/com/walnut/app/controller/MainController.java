package com.walnut.app.controller;

import com.walnut.app.model.Farmer;
import com.walnut.app.service.SystemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired private SystemService systemService;

    @GetMapping("/")
    public String home(HttpSession session) {
        // Kiểm tra xem đã đăng nhập chưa
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "index";
    }

    @GetMapping("/predict")
    public String predict(HttpSession session, Model model) {
        // Lấy thông tin Farmer từ phiên đăng nhập
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (user != null && "FARMER".equals(role)) {
            Farmer currentFarmer = (Farmer) user;

            // Tính toán cho chính nông dân này
            double prediction = systemService.predictYield(currentFarmer.getId());

            model.addAttribute("yieldVal", String.format("%.2f", prediction));
            model.addAttribute("farmerName", currentFarmer.getName());
            return "predict_yield";
        }

        return "redirect:/login";
    }

    // ... Giữ nguyên phần search ...
}