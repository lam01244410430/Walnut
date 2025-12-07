package com.walnut.app.controller;

import com.walnut.app.model.TrainingMaterial;      // [MỚI] Import Entity
import com.walnut.app.repository.TrainingRepository; // [MỚI] Import Repository
import org.springframework.beans.factory.annotation.Autowired; // [MỚI] Để tiêm dependency
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // [MỚI] Tiêm Repository để thao tác với Database
    @Autowired
    private TrainingRepository trainingRepo;

    // Đường dẫn lưu file vật lý trên máy tính
    private static final String UPLOAD_DIR = "./uploads/";

    // 1. Hiển thị trang Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("userCount", 128);
        model.addAttribute("totalArea", 540);
        model.addAttribute("pestWarnings", 12);
        return "admin/dashboard";
    }

    // 2. Hiển thị trang Upload
    @GetMapping("/upload")
    public String showUploadPage() {
        return "admin/upload";
    }

    // 3. Xử lý hành động Upload File (POST)
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("type") String type,
                                   @RequestParam("description") String description,
                                   Model model) {

        // Kiểm tra file rỗng
        if (file.isEmpty()) {
            model.addAttribute("error", "文件不能为空 (File không được rỗng)!");
            return "admin/upload";
        }

        try {
            // A. LƯU FILE VÀO Ổ CỨNG (Disk Storage)
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Copy file (Ghi đè nếu đã tồn tại)
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // B. LƯU THÔNG TIN VÀO DATABASE (New Logic)
            // Tạo đối tượng TrainingMaterial từ dữ liệu upload
            TrainingMaterial material = new TrainingMaterial();
            material.setTitle(fileName);                // Lấy tên file làm tiêu đề
            material.setDescription(description);       // Lấy mô tả từ form
            material.setFileType(type);                 // Lấy loại (pest_image, soil_data...)
            material.setFileSize(file.getSize());       // Lấy kích thước file
            material.setFilePath("/uploads/" + fileName); // Đường dẫn web (tương đối)

            // Gọi Repository để lưu xuống SQL
            trainingRepo.save(material);

            // Thông báo thành công
            String successMsg = "上传成功 (Đã lưu file và CSDL): " + fileName;
            model.addAttribute("message", successMsg);

            // Log kiểm tra
            System.out.println("--- Upload Info ---");
            System.out.println("File: " + filePath.toString());
            System.out.println("DB ID: " + material.getId()); // ID sẽ được tạo tự động sau khi save

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi I/O: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi Database: " + e.getMessage());
        }

        return "admin/upload";
    }
}