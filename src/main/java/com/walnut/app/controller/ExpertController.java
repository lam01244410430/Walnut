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
        Expert sessionUser = (Expert) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        Expert currentExpert = expertRepo.findById(sessionUser.getExpertID()).orElse(null);
        model.addAttribute("expert", currentExpert);

        // 1. QUERY DB: Tìm câu hỏi CHƯA trả lời dành cho chuyên gia này
        List<Consultation> pendingQuestions = consultationRepo.findByExpertAndAnswerIsNull(currentExpert);
        model.addAttribute("consultations", pendingQuestions);

        return "expert/dashboard";
    }

    // Xử lý khi chuyên gia bấm nút "Gửi" câu trả lời
    @PostMapping("/reply")
    public String replyQuestion(@RequestParam String consultationID,
                                @RequestParam String answer) {
        // 1. Tìm câu hỏi trong DB
        Consultation consult = consultationRepo.findById(consultationID).orElse(null);

        if (consult != null) {
            // 2. Cập nhật câu trả lời
            consult.setAnswer(answer);
            // 3. Lưu xuống DB thật
            consultationRepo.save(consult);
        }
        return "redirect:/expert/dashboard";
    }
}