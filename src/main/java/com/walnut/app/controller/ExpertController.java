package com.walnut.app.controller;

import com.walnut.app.model.Consultation;
import com.walnut.app.model.Expert;
import com.walnut.app.repository.ConsultationRepository;
import com.walnut.app.repository.ExpertRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/expert")
public class ExpertController {

    @Autowired private ExpertRepository expertRepo;
    @Autowired private ConsultationRepository consultationRepo;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // 1. Kiểm tra đăng nhập
        Expert sessionUser = (Expert) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        // 2. Lấy thông tin Chuyên gia mới nhất
        Expert currentExpert = expertRepo.findById(sessionUser.getExpertID()).orElse(null);
        model.addAttribute("expert", currentExpert);

        // 3. Lấy danh sách PENDING (Chưa trả lời)
        List<Consultation> pendingList = consultationRepo.findByExpertAndAnswerIsNull(currentExpert);
        model.addAttribute("pendingQuestions", pendingList);

        // 4. Lấy danh sách HISTORY (Đã trả lời)
        List<Consultation> historyList = consultationRepo.findByExpertAndAnswerIsNotNull(currentExpert);
        model.addAttribute("historyQuestions", historyList);

        return "expert/dashboard";
    }

    // --- XỬ LÝ TRẢ LỜI CÂU HỎI ---
    @PostMapping("/reply")
    public String handleReply(@RequestParam String consultationID,
                              @RequestParam String answer) {
        // Tìm câu hỏi trong DB
        Consultation consult = consultationRepo.findById(consultationID).orElse(null);

        if (consult != null) {
            // Cập nhật câu trả lời
            consult.setAnswer(answer);
            consultationRepo.save(consult); // Lưu xuống DB
        }

        return "redirect:/expert/dashboard"; // Load lại trang để thấy nó chuyển sang tab Lịch sử
    }
}