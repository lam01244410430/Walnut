package com.walnut.app.controller;

import com.walnut.app.model.Consultation;
import com.walnut.app.model.Farmer;
import com.walnut.app.model.ProductionData;
import com.walnut.app.repository.FarmerRepository;
import com.walnut.app.repository.ConsultationRepository; // Thêm repo này
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/farmer")
public class FarmerController {

    @Autowired private FarmerRepository farmerRepo;
    @Autowired private ConsultationRepository consultationRepo; // Dùng để lấy câu hỏi thật

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Farmer sessionUser = (Farmer) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        Farmer currentFarmer = farmerRepo.findById(sessionUser.getFarmerID()).orElse(null);
        model.addAttribute("farmer", currentFarmer);

        // 1. Lấy dữ liệu sản xuất và sắp xếp theo năm (2021 -> 2024)
        List<ProductionData> prodList = currentFarmer.getProductionDataList();
        prodList.sort(Comparator.comparingInt(ProductionData::getYear));

        // 2. Chuẩn bị dữ liệu cho Biểu đồ (Chart.js)
        // Chuỗi các năm: [2021, 2022, 2023, 2024]
        List<Integer> years = new ArrayList<>();
        // Chuỗi sản lượng: [4000, 4500, 2800, 3500]
        List<Double> yields = new ArrayList<>();

        for (ProductionData pd : prodList) {
            years.add(pd.getYear());
            yields.add(pd.getYield());
        }

        // 3. Tính dự đoán năm 2025 (Logic đơn giản: Trung bình cộng * 1.05)
        double predictedYield = 0;
        if (!yields.isEmpty()) {
            double sum = 0;
            for (Double y : yields) sum += y;
            double avg = sum / yields.size();
            predictedYield = avg * 1.05; // Dự đoán tăng nhẹ 5% so với trung bình
        }

        // Gửi dữ liệu sang HTML
        model.addAttribute("chartYears", years);
        model.addAttribute("chartYields", yields);
        model.addAttribute("predictedYield", Math.round(predictedYield)); // Làm tròn

        // ... logic lấy câu hỏi cũ giữ nguyên ...
        List<Consultation> myQuestions = consultationRepo.findByFarmer(currentFarmer);
        model.addAttribute("myQuestions", myQuestions);

        return "farmer/dashboard";
    }

    // Các mapping khác: /pest-search, /yield-predict...
}