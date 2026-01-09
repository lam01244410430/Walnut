package com.walnut.app.controller;

import com.walnut.app.model.*;
import com.walnut.app.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Cần cái này để xử lý file
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Cần cái này để hiện thông báo

import java.time.LocalDate;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private AdminRepository adminRepo;
    @Autowired private TrainingRepository materialRepo;
    @Autowired private FarmerRepository farmerRepo;
    @Autowired private ExpertRepository expertRepo;
    @Autowired private ProductionRepository prodRepo;
    @Autowired private ConsultationRepository consultationRepo;

    // --- 1. DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Admin sessionUser = (Admin) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        Admin currentAdmin = adminRepo.findById(sessionUser.getAdminID()).orElse(null);
        model.addAttribute("admin", currentAdmin);

        // --- A. TÍNH TOÁN SỐ LIỆU THỐNG KÊ (STATISTICS) ---

        // 1. Tổng người dùng (Total Users)
        long userCount = farmerRepo.count() + expertRepo.count() + adminRepo.count();
        model.addAttribute("userCount", userCount);

        // 2. Tổng diện tích (Total Area) & Dữ liệu biểu đồ
        List<ProductionData> allProduction = prodRepo.findAll();

        // Tính tổng diện tích từ tất cả các bản ghi (hoặc logic khác tùy bạn)
        // Ở đây tôi lấy tổng diện tích của các bản ghi mới nhất hoặc cộng dồn
        double totalArea = allProduction.stream().mapToDouble(ProductionData::getArea).sum();
        model.addAttribute("totalArea", Math.round(totalArea * 100.0) / 100.0); // Làm tròn 2 số lẻ

        // 3. Cảnh báo (Alerts) - Ở đây lấy số câu hỏi chưa trả lời làm cảnh báo
        long pendingCount = consultationRepo.findByAnswerIsNull().size();
        model.addAttribute("pestWarnings", pendingCount);

        // --- B. CHUẨN BỊ DỮ LIỆU BIỂU ĐỒ (CHART DATA) ---
        // Tổng hợp sản lượng của TOÀN BỘ hệ thống theo năm
        if (!allProduction.isEmpty()) {
            Map<Integer, Double> yieldByYear = new HashMap<>();

            for (ProductionData pd : allProduction) {
                yieldByYear.merge(pd.getYear(), pd.getYield(), Double::sum);
            }

            // Sắp xếp theo năm
            List<Integer> years = new ArrayList<>(yieldByYear.keySet());
            Collections.sort(years);

            List<Double> yields = new ArrayList<>();
            for (Integer year : years) {
                yields.add(yieldByYear.get(year));
            }

            model.addAttribute("chartData", true); // Cờ để hiện khung biểu đồ
            model.addAttribute("chartYears", years);
            model.addAttribute("chartYields", yields);
        } else {
            model.addAttribute("chartData", null);
        }

        return "admin/dashboard";
    }

    // --- 2. TRANG UPLOAD (GET) - KHẮC PHỤC LỖI CỦA BẠN TẠI ĐÂY ---
    // Hàm này sẽ chạy khi bạn bấm vào nút trên Sidebar
    @GetMapping("/upload")
    public String showUploadPage(HttpSession session, Model model) {
        Admin sessionUser = (Admin) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        // Gửi thông tin admin sang để hiển thị Sidebar/Header cho đẹp
        Admin currentAdmin = adminRepo.findById(sessionUser.getAdminID()).orElse(null);
        model.addAttribute("admin", currentAdmin);

        return "admin/upload"; // Trả về file templates/admin/upload.html
    }

    // --- 3. XỬ LÝ UPLOAD (POST) ---
    // Hàm này chạy khi bạn bấm nút "Submit" trong form upload.html
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("title") String title,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Admin sessionUser = (Admin) session.getAttribute("user");
        if (sessionUser != null) {
            Admin dbAdmin = adminRepo.findById(sessionUser.getAdminID()).orElse(null);

            if (dbAdmin != null && !file.isEmpty()) {
                try {
                    // --- BƯỚC 1: TẠO THƯ MỤC LƯU TRỮ ---
                    String uploadDir = System.getProperty("user.dir") + "/uploads/";
                    File dir = new File(uploadDir);
                    if (!dir.exists()) dir.mkdirs();

                    // --- BƯỚC 2: LƯU FILE ---
                    String originalFilename = file.getOriginalFilename();
                    File serverFile = new File(uploadDir + originalFilename);
                    file.transferTo(serverFile);

                    // --- BƯỚC 3: TÍNH TOÁN MATERIAL_ID TỰ TĂNG ---
                    String nextID = "MAT-001"; // Mặc định
                    TrainingMaterial lastMat = materialRepo.findTopByOrderByMaterialIDDesc();

                    if (lastMat != null) {
                        String currentMaxID = lastMat.getMaterialID();
                        // Kiểm tra xem ID cũ có đúng định dạng "MAT-" không
                        if (currentMaxID != null && currentMaxID.startsWith("MAT-")) {
                            try {
                                // Tách phần số: MAT-005 -> 5
                                int number = Integer.parseInt(currentMaxID.split("-")[1]);
                                // Tăng lên 1 và format lại thành 006
                                nextID = String.format("MAT-%03d", number + 1);
                            } catch (Exception e) {
                                // Nếu ID cũ không đúng chuẩn số thì reset về 001
                                nextID = "MAT-001";
                            }
                        }
                    }

                    // --- BƯỚC 4: LƯU VÀO DATABASE ---
                    TrainingMaterial material = new TrainingMaterial();
                    material.setMaterialID(nextID); // ID tự tăng
                    material.setContent(title + " (File: " + originalFilename + ")");

                    // LocalDate.now() sẽ lưu vào DB dưới dạng yyyy-MM-dd
                    material.setUploadDate(LocalDate.now());
                    material.setAdmin(dbAdmin);

                    materialRepo.save(material);

                    redirectAttributes.addFlashAttribute("message", "上传成功!");

                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
                }
            } else {
                redirectAttributes.addFlashAttribute("message", "Error: Empty File.");
            }
        }
        return "redirect:/admin/upload";
    }

    // --- 4. QUẢN LÝ USER (Inner Class DTO) ---
    public static class UserRow {
        private String id;
        private String username;
        private String name;
        private String role;
        private String password;

        public UserRow() {}
        public UserRow(String id, String username, String name, String role) {
            this.id = id;
            this.username = username;
            this.name = name;
            this.role = role;
        }
        // Getters Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @GetMapping("/users")
    public String listUsers(@RequestParam(value = "keyword", required = false) String keyword,
                            Model model,
                            HttpSession session) {

        Admin sessionUser = (Admin) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";
        model.addAttribute("admin", sessionUser);

        // 1. GOM DỮ LIỆU TỪ 3 BẢNG
        List<UserRow> allUsers = new ArrayList<>();

        // Lấy ADMIN
        for (Admin a : adminRepo.findAll()) {
            allUsers.add(new UserRow(a.getAdminID(), a.getUsername(), a.getName(), "ADMIN"));
        }
        // Lấy EXPERT
        for (Expert e : expertRepo.findAll()) {
            allUsers.add(new UserRow(e.getExpertID(), e.getUsername(), e.getName(), "EXPERT"));
        }
        // Lấy FARMER
        for (Farmer f : farmerRepo.findAll()) {
            allUsers.add(new UserRow(f.getFarmerID(), f.getUsername(), f.getName(), "FARMER"));
        }

        // 2. XỬ LÝ TÌM KIẾM (SEARCH)
        // Nếu có từ khóa, lọc danh sách lại
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKey = keyword.toLowerCase(); // Chuyển về chữ thường để tìm không phân biệt hoa thường

            allUsers = allUsers.stream()
                    .filter(u ->
                            u.getUsername().toLowerCase().contains(searchKey) || // Tìm theo Username (SĐT)
                                    u.getId().toLowerCase().contains(searchKey) ||       // Tìm theo ID
                                    u.getRole().toLowerCase().contains(searchKey) ||     // Tìm theo Role
                                    u.getName().toLowerCase().contains(searchKey)        // Tìm theo Tên (Khuyến mãi thêm)
                    )
                    .collect(Collectors.toList());

            // Gửi lại từ khóa ra giao diện để giữ trong ô input
            model.addAttribute("keyword", keyword);
        }

        // 3. XỬ LÝ SẮP XẾP (SORT BY ROLE)
        // Thứ tự ưu tiên: ADMIN -> EXPERT -> FARMER
        allUsers.sort(Comparator.comparing(UserRow::getRole));

        model.addAttribute("users", allUsers);
        return "admin/users";
    }

    private String generateNextId(String prefix, String currentMaxId) {
        // Mặc định nếu chưa có ai thì bắt đầu từ 001
        String nextId = prefix + "-001";

        if (currentMaxId != null && currentMaxId.startsWith(prefix + "-")) {
            try {
                // Tách số: FARMER-005 -> 5
                String numberPart = currentMaxId.split("-")[1];
                int number = Integer.parseInt(numberPart);

                // Tăng lên 1 và format lại 3 chữ số: 6 -> 006
                nextId = String.format("%s-%03d", prefix, number + 1);
            } catch (Exception e) {
                // Nếu ID cũ sai định dạng, reset về 001
            }
        }
        return nextId;
    }

    // --- SỬA HÀM ADD USER ---
    @PostMapping("/users/add")
    public String addUser(@ModelAttribute UserRow form) {

        switch (form.getRole()) {
            case "FARMER":
                // 1. Tìm ID lớn nhất hiện tại
                Farmer lastFarmer = farmerRepo.findTopByOrderByFarmerIDDesc();
                String maxFarmerId = (lastFarmer != null) ? lastFarmer.getFarmerID() : null;

                // 2. Tính ID mới
                String newFarmerId = generateNextId("FARMER", maxFarmerId);

                // 3. Tạo và lưu
                Farmer f = new Farmer();
                f.setFarmerID(newFarmerId);
                f.setUsername(form.getUsername());
                f.setPhone(form.getUsername());
                f.setName(form.getName());
                f.setPassword(form.getPassword());
                farmerRepo.save(f);
                break;

            case "EXPERT":
                Expert lastExpert = expertRepo.findTopByOrderByExpertIDDesc();
                String maxExpertId = (lastExpert != null) ? lastExpert.getExpertID() : null;
                String newExpertId = generateNextId("EXPERT", maxExpertId);

                Expert e = new Expert();
                e.setExpertID(newExpertId);
                e.setUsername(form.getUsername());
                e.setPhone(form.getUsername());
                e.setName(form.getName());
                e.setPassword(form.getPassword());
                expertRepo.save(e);
                break;

            case "ADMIN":
                Admin lastAdmin = adminRepo.findTopByOrderByAdminIDDesc();
                String maxAdminId = (lastAdmin != null) ? lastAdmin.getAdminID() : null;
                String newAdminId = generateNextId("ADMIN", maxAdminId);

                Admin a = new Admin();
                a.setAdminID(newAdminId);
                a.setUsername(form.getUsername());
                a.setName(form.getName());
                a.setPassword(form.getPassword());
                adminRepo.save(a);
                break;
        }
        return "redirect:/admin/users";
    }

    // ... Các hàm update/delete giữ nguyên như cũ
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable String id) {
        if (id.startsWith("FARMER")) farmerRepo.deleteById(id);
        else if (id.startsWith("EXPERT")) expertRepo.deleteById(id);
        else if (id.startsWith("ADMIN")) adminRepo.deleteById(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute UserRow form) {
        // ... (Giữ nguyên logic update như lần trước)
        String id = form.getId();
        String role = form.getRole();

        if ("FARMER".equals(role)) {
            Farmer f = farmerRepo.findById(id).orElse(null);
            if (f != null) {
                f.setName(form.getName());
                f.setUsername(form.getUsername());
                f.setPhone(form.getUsername());
                if (form.getPassword() != null && !form.getPassword().isEmpty()) f.setPassword(form.getPassword());
                farmerRepo.save(f);
            }
        } else if ("EXPERT".equals(role)) {
            Expert e = expertRepo.findById(id).orElse(null);
            if (e != null) {
                e.setName(form.getName());
                e.setUsername(form.getUsername());
                e.setPhone(form.getUsername());
                if (form.getPassword() != null && !form.getPassword().isEmpty()) e.setPassword(form.getPassword());
                expertRepo.save(e);
            }
        } else if ("ADMIN".equals(role)) {
            Admin a = adminRepo.findById(id).orElse(null);
            if (a != null) {
                a.setName(form.getName());
                a.setUsername(form.getUsername());
                if (form.getPassword() != null && !form.getPassword().isEmpty()) a.setPassword(form.getPassword());
                adminRepo.save(a);
            }
        }
        return "redirect:/admin/users";
    }
}