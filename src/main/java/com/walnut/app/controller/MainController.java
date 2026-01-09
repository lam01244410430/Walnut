package com.walnut.app.controller;

import com.walnut.app.model.*;
import com.walnut.app.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class MainController {
    @Autowired private FarmerRepository farmerRepo;
    @Autowired private AdminRepository adminRepo;
    @Autowired private ExpertRepository expertRepo;
    @Autowired private ConsultationRepository consultationRepo;
    @Autowired private TrainingRepository trainingRepo;

    // Thêm Repo này để tìm kiếm sâu bệnh
    @Autowired private PestRepository pestRepo;

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
            return "redirect:/";
        }

        // 2. KIỂM TRA EXPERT
        Expert expert = expertRepo.findByUsername(username);
        if (expert != null && expert.getPassword().equals(password)) {
            session.setAttribute("user", expert);
            session.setAttribute("role", "EXPERT");

            // --- THAY ĐỔI Ở ĐÂY ---
            // Chuyển hướng đến đường dẫn dành riêng cho Expert
            return "redirect:/expert/index";
        }

        // 3. KIỂM TRA FARMER
        Farmer farmer = farmerRepo.findByUsername(username);
        if (farmer != null && farmer.getPassword().equals(password)) {
            session.setAttribute("user", farmer);
            session.setAttribute("role", "FARMER");
            return "redirect:/";
        }

        model.addAttribute("error", "登录失败：用户名或密码错误 (Sai tên đăng nhập hoặc mật khẩu)");
        return "login";
    }

    // --- CÁC MAPPING BẠN YÊU CẦU (Đã thêm logic xử lý) ---

    // Route cho nút '去查询' (Tra cứu sâu bệnh)
    @GetMapping("/search_pest")
    public String searchPestPage(@RequestParam(value = "keyword", required = false) String keyword,
                                 Model model) {
        // Logic tìm kiếm đã chuyển sang đây
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<PestDisease> results = pestRepo.findBySymptomsContainingIgnoreCaseOrNameContainingIgnoreCase(keyword, keyword);
            model.addAttribute("results", results);
            model.addAttribute("keyword", keyword);
        }
        return "search_pest"; // Đảm bảo tên file html là search_pest.html
    }

    // Route cho nút '看数据' (Dự đoán sản lượng)
    @GetMapping("/predict_yield")
    public String predictYieldPage(HttpSession session, Model model) {
        // Kiểm tra đăng nhập
        Object user = session.getAttribute("user");
        if (!(user instanceof Farmer)) return "redirect:/login"; // Chỉ farmer mới xem được

        Farmer sessionUser = (Farmer) user;
        Farmer currentFarmer = farmerRepo.findById(sessionUser.getFarmerID()).orElse(null);

        model.addAttribute("farmer", currentFarmer);

        // Gọi hàm vẽ biểu đồ
        prepareChartData(currentFarmer, model);

        return "predict_yield"; // Đảm bảo tên file html trùng khớp (yield-predict.html hoặc predict_yield.html)
    }

    // Route cho nút '问专家' (Tư vấn chuyên gia)
    @GetMapping("/consultation")
    public String consultationPage(HttpSession session, Model model) {
        // Lấy user từ session
        Object user = session.getAttribute("user");

        // Kiểm tra xem user có phải là Farmer không
        if (user instanceof Farmer) {
            Farmer currentFarmer = (Farmer) user;

            // Lấy danh sách câu hỏi của nông dân này (Sắp xếp mới nhất lên đầu nếu muốn)
            List<Consultation> myQuestions = consultationRepo.findByFarmer(currentFarmer);

            // Đẩy dữ liệu sang HTML
            model.addAttribute("myQuestions", myQuestions);
        }

        return "consultation";
    }

    // --- HÀM PHỤ TRỢ (Để vẽ biểu đồ) ---
    private void prepareChartData(Farmer farmer, Model model) {
        List<ProductionData> prodList = farmer.getProductionDataList();

        if (prodList != null) {
            prodList.sort(Comparator.comparingInt(ProductionData::getYear));

            List<Integer> years = new ArrayList<>();
            List<Double> yields = new ArrayList<>();

            for (ProductionData pd : prodList) {
                years.add(pd.getYear());
                yields.add(pd.getYield());
            }

            double predictedYield = 0;
            if (!yields.isEmpty()) {
                double sum = 0;
                for (Double y : yields) sum += y;
                double avg = sum / yields.size();
                predictedYield = avg * 1.05;
            }

            model.addAttribute("chartYears", years);
            model.addAttribute("chartYields", yields);
            model.addAttribute("predictedYield", Math.round(predictedYield));
        }
    }

    // --- 1. HIỂN THỊ TRANG ĐĂNG KÝ (GET) ---
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // --- HÀM PHỤ TRỢ: SINH ID TỰ TĂNG (VD: FARMER-005 -> FARMER-006) ---
    private String generateNextId(String prefix, String currentMaxId) {
        String nextId = prefix + "-001"; // Mặc định
        if (currentMaxId != null && currentMaxId.startsWith(prefix + "-")) {
            try {
                // Lấy phần số sau dấu gạch ngang
                String numberPart = currentMaxId.split("-")[1];
                int number = Integer.parseInt(numberPart);
                // Tăng lên 1 và format 3 chữ số
                nextId = String.format("%s-%03d", prefix, number + 1);
            } catch (Exception e) {
                // Nếu lỗi format cũ thì reset về 001
            }
        }
        return nextId;
    }

    // --- 2. XỬ LÝ ĐĂNG KÝ (POST) ---
    @PostMapping("/register")
    public String handleRegister(
            @RequestParam String role,          // FARMER hoặc EXPERT
            @RequestParam String username,      // Bắt buộc
            @RequestParam String password,      // Bắt buộc
            @RequestParam String name,          // Bắt buộc
            @RequestParam String phone,         // Bắt buộc
            // Các trường tùy chọn (không bắt buộc vì phụ thuộc Role)
            @RequestParam(required = false) String address,   // Chỉ Farmer
            @RequestParam(required = false) String email,     // Chỉ Expert (nhưng JS đã check required)
            @RequestParam(required = false) String specialty, // Chỉ Expert
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1. Kiểm tra Username đã tồn tại chưa (trên cả 3 bảng)
        if (farmerRepo.findByUsername(username) != null ||
                expertRepo.findByUsername(username) != null ||
                adminRepo.findByUsername(username) != null) {

            model.addAttribute("error", "注册失败：用户名已存在 (Username đã tồn tại)!");
            return "register";
        }

        try {
            if ("FARMER".equals(role)) {
                // --- XỬ LÝ FARMER ---
                // a. Lấy ID lớn nhất để tính ID mới
                Farmer lastFarmer = farmerRepo.findTopByOrderByFarmerIDDesc();
                String maxId = (lastFarmer != null) ? lastFarmer.getFarmerID() : null;
                String nextId = generateNextId("FARMER", maxId);

                // b. Tạo đối tượng
                Farmer f = new Farmer();
                f.setFarmerID(nextId);
                f.setUsername(username);
                f.setPassword(password);
                f.setName(name);
                f.setPhone(phone);
                f.setAddress(address); // Set địa chỉ

                // c. Lưu
                farmerRepo.save(f);

            } else if ("EXPERT".equals(role)) {
                // --- XỬ LÝ EXPERT ---
                // a. Lấy ID lớn nhất
                Expert lastExpert = expertRepo.findTopByOrderByExpertIDDesc();
                String maxId = (lastExpert != null) ? lastExpert.getExpertID() : null;
                String nextId = generateNextId("EXPERT", maxId);

                // b. Tạo đối tượng
                Expert e = new Expert();
                e.setExpertID(nextId);
                e.setUsername(username);
                e.setPassword(password);
                e.setName(name);
                e.setPhone(phone);
                e.setEmail(email);       // Set email
                e.setSpecialty(specialty); // Set chuyên môn

                // c. Lưu
                expertRepo.save(e);
            }

            // 3. Đăng ký thành công -> Chuyển về Login
            redirectAttributes.addFlashAttribute("successMessage", "注册成功！请登录 (Đăng ký thành công, vui lòng đăng nhập).");
            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "系统错误: " + e.getMessage());
            return "register";
        }
    }
    @GetMapping("/search_training")
    public String searchTrainingPage(@RequestParam(value = "keyword", required = false) String keyword,
                                     Model model) {

        List<TrainingMaterial> results;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Nếu có từ khóa -> Tìm trong DB
            results = trainingRepo.findByContentContainingIgnoreCaseOrderByUploadDateDesc(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            // Nếu không có từ khóa -> Hiển thị tất cả (Mặc định)
            results = trainingRepo.findAllByOrderByUploadDateDesc();
        }

        model.addAttribute("materials", results);
        return "search_training"; // Trả về file search_training.html
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filename) {
        try {
            // Đường dẫn đến thư mục uploads (nơi AdminController đã lưu file)
            Path filePath = Paths.get(System.getProperty("user.dir") + "/uploads/").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        // Header này báo cho trình duyệt biết đây là file cần tải xuống
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}