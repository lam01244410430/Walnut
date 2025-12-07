package com.walnut.app.controller;

import com.walnut.app.model.Consultation;
import com.walnut.app.model.Expert;
import com.walnut.app.repository.ConsultationRepository;
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
@RequestMapping("/expert") // 路径前缀 (Tiền tố đường dẫn)
public class ExpertController {

    @Autowired
    private ConsultationRepository consultationRepo;

    /**
     * 1. 专家工作台 (Màn hình làm việc của chuyên gia)
     * Hiển thị danh sách câu hỏi từ nông dân.
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // A. 权限检查 (Kiểm tra quyền: Phải là EXPERT)
        Object user = session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (user == null || !"EXPERT".equals(role)) {
            return "redirect:/login"; // 未登录则跳转 (Chưa đăng nhập thì đá về login)
        }

        // B. 获取当前专家信息 (Lấy thông tin chuyên gia hiện tại)
        Expert currentExpert = (Expert) user;
        model.addAttribute("expertName", currentExpert.getName());          // 姓名 (Tên)
        model.addAttribute("specialization", currentExpert.getSpecialization()); // 专业 (Chuyên môn)

        // C. 获取咨询列表 (Lấy danh sách câu hỏi từ Database)
        // 实际应用中可能需要分页 (Trong thực tế có thể phân trang, ở đây lấy hết)
        List<Consultation> questions = consultationRepo.findAll();
        model.addAttribute("questions", questions);

        return "expert/dashboard"; // 返回视图 (Trả về file HTML)
    }

    /**
     * 2. 回复咨询 (Trả lời câu hỏi)
     * Nhận câu trả lời từ form và lưu vào Database.
     */
    @PostMapping("/reply")
    public String replyToQuestion(@RequestParam Long questionId,
                                  @RequestParam String answerText,
                                  HttpSession session) {

        // A. 再次检查权限 (Kiểm tra lại quyền cho chắc chắn)
        if (!"EXPERT".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Expert currentExpert = (Expert) session.getAttribute("user");

        // B. 查找问题 (Tìm câu hỏi theo ID)
        Consultation consult = consultationRepo.findById(questionId).orElse(null);

        if (consult != null) {
            // C. 格式化回复内容 (Định dạng câu trả lời chuẩn tiếng Trung)
            // Format: "Tên chuyên gia (Chuyên môn): Nội dung"
            // Ví dụ: "李文教授 (植物病理学): 建议使用..."
            String signature = currentExpert.getName() + " (" + currentExpert.getSpecialization() + "): ";
            String fullAnswer = signature + answerText;

            // D. 保存到数据库 (Lưu xuống DB)
            consult.setAnswer(fullAnswer);
            consultationRepo.save(consult);

            System.out.println("✅ 已回复: Question ID " + questionId);
        }

        // E. 刷新页面 (Load lại trang Dashboard)
        return "redirect:/expert/dashboard";
    }
}