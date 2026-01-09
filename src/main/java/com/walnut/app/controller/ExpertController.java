package com.walnut.app.controller;

import com.walnut.app.model.Consultation;
import com.walnut.app.model.Expert;
import com.walnut.app.repository.ConsultationRepository;
import com.walnut.app.repository.ExpertRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/expert")
public class ExpertController {

    @Autowired private ExpertRepository expertRepo;
    @Autowired private ConsultationRepository consultationRepo;

    // --- 1. TRANG CHỦ MỚI CHO EXPERT ---
    @GetMapping("/index")
    public String expertIndex(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Expert)) return "redirect:/login";

        // Lấy danh sách câu hỏi CHƯA trả lời để hiển thị ngay trang chủ
        // (Đảm bảo bạn đã có hàm findByAnswerIsNull trong Repo như hướng dẫn trước)
        List<Consultation> pendingQuestions = consultationRepo.findByAnswerIsNull();
        model.addAttribute("pendingQuestions", pendingQuestions);

        // Trả về file template 'index_expert.html' (nằm cùng cấp với index.html)
        return "index_expert";
    }

    // --- 2. DASHBOARD (Chỉ còn Lịch sử & Info) ---
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Expert)) return "redirect:/login";

        Expert sessionExpert = (Expert) user;
        // Load lại thông tin mới nhất
        Expert currentExpert = expertRepo.findById(sessionExpert.getExpertID()).orElse(sessionExpert);
        model.addAttribute("expert", currentExpert);

        // Lấy danh sách câu hỏi ĐÃ trả lời của chuyên gia này
        // (Bạn có thể lọc trong Java hoặc viết query trong Repo)
        List<Consultation> history = currentExpert.getConsultationList();

        model.addAttribute("history", history);

        // Trả về file trong thư mục templates/expert/dashboard.html
        return "expert/dashboard";
    }

    // --- 3. XỬ LÝ TRẢ LỜI CÂU HỎI ---
    @PostMapping("/reply")
    public String replyToFarmer(@RequestParam("consultationID") String consultationID,
                                @RequestParam("answer") String answer,
                                HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Expert)) return "redirect:/login";
        Expert currentExpert = (Expert) user;

        Consultation consult = consultationRepo.findById(consultationID).orElse(null);
        if (consult != null) {
            consult.setAnswer(answer);
            consult.setExpert(currentExpert);
            consultationRepo.save(consult);
        }

        // Trả lời xong thì quay lại trang chủ Expert để làm việc tiếp
        return "redirect:/expert/index";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Expert expertForm,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        // 1. Kiểm tra đăng nhập
        Object user = session.getAttribute("user");
        if (!(user instanceof Expert)) return "redirect:/login";

        Expert sessionExpert = (Expert) user;

        // 2. Lấy expert thực tế từ DB
        Expert currentExpert = expertRepo.findById(sessionExpert.getExpertID()).orElse(null);

        if (currentExpert != null) {
            // 3. Cập nhật các trường cho phép sửa
            currentExpert.setName(expertForm.getName());
            currentExpert.setPhone(expertForm.getPhone());
            currentExpert.setEmail(expertForm.getEmail());
            currentExpert.setSpecialty(expertForm.getSpecialty());

            // Lưu vào DB
            expertRepo.save(currentExpert);

            // Cập nhật lại Session để giao diện hiển thị đúng ngay lập tức
            session.setAttribute("user", currentExpert);

            // Gửi thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "个人信息已更新!");
        }

        return "redirect:/expert/dashboard";
    }
}