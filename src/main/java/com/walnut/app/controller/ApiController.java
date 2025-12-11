package com.walnut.app.controller;

import com.walnut.app.model.*;
import com.walnut.app.repository.*;
import com.walnut.app.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // Trả về JSON thay vì HTML
@RequestMapping("/api") // Tất cả link bắt đầu bằng /api (VD: /api/pests)
public class ApiController {

    @Autowired private SystemService systemService;
    @Autowired private FarmerRepository farmerRepo;
    @Autowired private AdminRepository adminRepo;

    // Bạn cần tạo thêm ConsultationRepository và TrainingMaterialRepository
    // Tôi giả định bạn đã tạo chúng tương tự PestRepository
    // @Autowired private ConsultationRepository consultationRepo;
    // @Autowired private TrainingMaterialRepository trainingRepo;

    // ==========================================
    // 1. CHỨC NĂNG TRA CỨU BỆNH (病害查询)
    // ==========================================
    @GetMapping("/pests")
    public List<PestDisease> searchPests(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return systemService.searchPest(""); // Trả về tất cả nếu không có từ khóa
        }
        return systemService.searchPest(keyword);
    }

    // ==========================================
    // 2. DỰ BÁO SẢN LƯỢNG (产量预测)
    // ==========================================
    @GetMapping("/predict/{farmerId}")
    public ResponseEntity<?> predictYield(@PathVariable String farmerId) {
        // Kiểm tra nông dân có tồn tại không
        if (!farmerRepo.existsById(farmerId)) {
            return ResponseEntity.badRequest().body("错误: 找不到农民!");
        }

        double prediction = systemService.predictYield(farmerId);

        // Trả về JSON object
        Map<String, Object> response = new HashMap<>();
        response.put("farmerId", farmerId);
        response.put("predictedYield", prediction);
        response.put("unit", "kg (公斤)");
        response.put("message", "基于历史数据增长10%");

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 3. ĐĂNG NHẬP API (cho Mobile App)
    // ==========================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        String role = loginData.get("role"); // "FARMER" hoặc "ADMIN"

        if ("FARMER".equals(role)) {
            Farmer farmer = farmerRepo.findByUsername(username);
            if (farmer != null && farmer.getPassword().equals(password)) {
                return ResponseEntity.ok(farmer); // Trả về thông tin nông dân (JSON)
            }
        } else if ("ADMIN".equals(role)) {
            Admin admin = adminRepo.findByUsername(username);
            if (admin != null && admin.getPassword().equals(password)) {
                return ResponseEntity.ok(admin);
            }
        }

        return ResponseEntity.status(401).body("登录失败: 用户名或密码错误 (Đăng nhập thất bại)");
    }

    // ==========================================
    // 4. CHUYÊN GIA ĐỐI THOẠI (专家对话) - Demo
    // ==========================================
    @PostMapping("/consultation")
    public ResponseEntity<?> askExpert(@RequestBody Map<String, Object> request) {
        // Giả lập nhận câu hỏi từ JSON: { "farmerId": 1, "question": "Lá bị vàng?" }
        Long farmerId = Long.valueOf(request.get("farmerId").toString());
        String question = (String) request.get("question");

        // Ở đây bạn sẽ gọi consultationRepo.save(...)
        // Vì chưa có Repo này, tôi trả về thông báo giả lập thành công

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "问题已发送给专家 (Câu hỏi đã gửi đến chuyên gia)");
        response.put("ticketId", "TICKET-" + System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}