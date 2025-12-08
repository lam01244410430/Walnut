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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
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

    // --- 1. DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Admin sessionUser = (Admin) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        Admin currentAdmin = adminRepo.findById(sessionUser.getAdminID()).orElse(null);
        model.addAttribute("admin", currentAdmin);
        model.addAttribute("materials", materialRepo.findAll());
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

            // Kiểm tra admin tồn tại VÀ file không rỗng
            if (dbAdmin != null && !file.isEmpty()) {
                try {
                    // --- BƯỚC 1: TẠO THƯ MỤC LƯU TRỮ ---
                    // Lấy đường dẫn thư mục gốc của dự án + "/uploads/"
                    String uploadDir = System.getProperty("user.dir") + "/uploads/";
                    File dir = new File(uploadDir);

                    // Nếu thư mục chưa có thì tạo mới
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    // --- BƯỚC 2: LƯU FILE VÀO Ổ CỨNG ---
                    String originalFilename = file.getOriginalFilename();
                    // Tạo đường dẫn file đích
                    File serverFile = new File(uploadDir + originalFilename);

                    // Lệnh quan trọng nhất: Ghi dữ liệu file vào ổ cứng
                    file.transferTo(serverFile);

                    // --- BƯỚC 3: LƯU THÔNG TIN VÀO DATABASE ---
                    TrainingMaterial material = new TrainingMaterial();
                    material.setMaterialID("MAT-" + UUID.randomUUID().toString().substring(0, 6));

                    // Lưu nội dung là Tiêu đề + Tên file đã lưu
                    material.setContent(title + " (File: " + originalFilename + ")");

                    material.setUploadDate(LocalDate.now());
                    material.setAdmin(dbAdmin);

                    materialRepo.save(material);

                    // Thông báo thành công kèm đường dẫn để bạn dễ kiểm tra
                    redirectAttributes.addFlashAttribute("message", "上传成功！");

                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("message", "文件保存失败： " + e.getMessage());
                }
            } else {
                redirectAttributes.addFlashAttribute("message", "错误：文件为空或未选择文件。");
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

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute UserRow form) {
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        switch (form.getRole()) {
            case "FARMER":
                Farmer f = new Farmer();
                f.setFarmerID("FARMER-" + uuid);
                f.setUsername(form.getUsername());
                f.setPhone(form.getUsername());
                f.setName(form.getName());
                f.setPassword(form.getPassword());
                farmerRepo.save(f);
                break;
            case "EXPERT":
                Expert e = new Expert();
                e.setExpertID("EXPERT-" + uuid);
                e.setUsername(form.getUsername());
                e.setPhone(form.getUsername());
                e.setName(form.getName());
                e.setPassword(form.getPassword());
                expertRepo.save(e);
                break;
            case "ADMIN":
                Admin a = new Admin();
                a.setAdminID("ADMIN-" + uuid);
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