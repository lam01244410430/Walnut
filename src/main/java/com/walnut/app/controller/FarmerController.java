package com.walnut.app.controller;

import com.walnut.app.model.Consultation;
import com.walnut.app.model.Farmer;
import com.walnut.app.model.ProductionData;
import com.walnut.app.model.TrainingMaterial;
import com.walnut.app.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
public class FarmerController {

    @Autowired private FarmerRepository farmerRepo;
    @Autowired private ConsultationRepository consultationRepo;
    @Autowired private ProductionRepository prodRepo;
    @Autowired private TrainingRepository trainingRepo;

    // --- 1. DASHBOARD ---
    @GetMapping("/farmer/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Farmer sessionUser = (Farmer) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        Farmer currentFarmer = farmerRepo.findById(sessionUser.getFarmerID()).orElse(null);
        model.addAttribute("farmer", currentFarmer);

        // A. Load dữ liệu biểu đồ (Đã sửa lỗi trùng năm)
        prepareChartData(currentFarmer, model);

        // B. Lấy danh sách câu hỏi
        List<Consultation> myQuestions = consultationRepo.findByFarmer(currentFarmer);
        model.addAttribute("myQuestions", myQuestions);

        return "farmer/dashboard";
    }

    // --- HÀM PHỤ TRỢ: XỬ LÝ DỮ LIỆU BIỂU ĐỒ (QUAN TRỌNG) ---
    private void prepareChartData(Farmer farmer, Model model) {
        List<ProductionData> prodList = farmer.getProductionDataList();

        if (prodList != null && !prodList.isEmpty()) {
            // 1. Dùng Map để cộng dồn sản lượng theo năm
            // Key: Năm, Value: Tổng sản lượng
            Map<Integer, Double> yieldByYear = new HashMap<>();

            for (ProductionData pd : prodList) {
                // Nếu năm đã có, cộng thêm yield mới. Nếu chưa, tạo mới.
                yieldByYear.merge(pd.getYear(), pd.getYield(), Double::sum);
            }

            // 2. Tách ra danh sách để hiển thị và Sắp xếp theo năm
            List<Integer> years = new ArrayList<>(yieldByYear.keySet());
            Collections.sort(years); // Sắp xếp năm tăng dần (2020, 2021, ...)

            List<Double> yields = new ArrayList<>();
            for (Integer year : years) {
                yields.add(yieldByYear.get(year));
            }

            // 3. Tính dự đoán (Dựa trên trung bình cộng * 105%)
            double predictedYield = 0;
            if (!yields.isEmpty()) {
                double sum = 0;
                for (Double y : yields) sum += y;
                double avg = sum / yields.size();
                predictedYield = avg * 1.05;
            }

            // 4. Đẩy dữ liệu ra Model
            model.addAttribute("chartYears", years);
            model.addAttribute("chartYields", yields);
            model.addAttribute("predictedYield", Math.round(predictedYield));
        } else {
            // Xử lý trường hợp chưa có dữ liệu
            model.addAttribute("chartYears", new ArrayList<>());
            model.addAttribute("chartYields", new ArrayList<>());
            model.addAttribute("predictedYield", 0);
        }
    }

    // --- 2. XỬ LÝ THÊM DỮ LIỆU SẢN LƯỢNG (POST) ---
    @PostMapping("/add-production-data")
    public String addProductionData(@RequestParam("yield") Double yield,
                                    @RequestParam("year") int year,
                                    HttpSession session) {
        Farmer sessionUser = (Farmer) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        Farmer currentFarmer = farmerRepo.findById(sessionUser.getFarmerID()).orElse(null);

        ProductionData newData = new ProductionData();

        // --- TẠO DATA_ID THEO CÔNG THỨC: DB + FarmerID_Suffix + Year ---
        String farmerID = currentFarmer.getFarmerID(); // Ví dụ: FARMER-001
        String farmerSuffix = "";

        if (farmerID.contains("-")) {
            // Lấy phần sau dấu gạch ngang (ví dụ: 001)
            farmerSuffix = farmerID.split("-")[1];
        } else {
            // Trường hợp ID không có gạch ngang, lấy nguyên chuỗi
            farmerSuffix = farmerID;
        }

        // Tạo ID: Ví dụ DB0012021
        String customDataID = "DB" + farmerSuffix + year;
        newData.setDataID(customDataID);
        // -------------------------------------------------------------

        newData.setYield(yield);
        newData.setYear(year);
        newData.setArea(1.0); // Mặc định hoặc lấy từ form nếu có

        // LocalDate.now() tự động lưu format yyyy-MM-dd chuẩn SQL
        newData.setDate(LocalDate.now());

        newData.setFarmer(currentFarmer);

        // Lưu vào DB (Nếu ID này đã tồn tại cho năm đó, nó sẽ đè lên/cập nhật)
        prodRepo.save(newData);

        return "redirect:/predict_yield";
    }

    // ... (Các hàm khác như training, addQuestion, updateProfile giữ nguyên) ...

    // --- COPY LẠI CÁC HÀM KHÁC ĐỂ FILE HOÀN CHỈNH ---
    @GetMapping("/training-materials")
    public String trainingList(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        List<TrainingMaterial> materials = trainingRepo.findAllByOrderByUploadDateDesc();
        model.addAttribute("materials", materials);
        return "training-list";
    }

    @GetMapping("/training-detail")
    public String trainingDetail(@RequestParam("id") String id, Model model) {
        TrainingMaterial mat = trainingRepo.findById(id).orElse(null);
        model.addAttribute("material", mat);
        return "training-detail";
    }

    @PostMapping("/add-question")
    public String addQuestion(@RequestParam("questionContent") String content,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Farmer sessionUser = (Farmer) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";
        Farmer currentFarmer = farmerRepo.findById(sessionUser.getFarmerID()).orElse(null);

        // TÍNH TOÁN ID TỰ ĐỘNG TĂNG (Như đã hướng dẫn trước đó)
        String nextID = "CONSULT-001";
        Consultation lastCons = consultationRepo.findTopByOrderByConsultationIDDesc();
        if (lastCons != null && lastCons.getConsultationID().startsWith("CONSULT-")) {
            try {
                int num = Integer.parseInt(lastCons.getConsultationID().split("-")[1]) + 1;
                nextID = String.format("CONSULT-%03d", num);
            } catch (Exception e) {}
        }

        Consultation q = new Consultation();
        q.setConsultationID(nextID);
        q.setQuestion(content);
        q.setFarmer(currentFarmer);
        q.setCreatedDate(LocalDate.now());
        consultationRepo.save(q);

        redirectAttributes.addFlashAttribute("successMessage", "提交成功！");
        return "redirect:/consultation";
    }

    @PostMapping("/farmer/update-profile")
    public String updateProfile(@ModelAttribute Farmer farmerForm,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Farmer)) return "redirect:/login";
        Farmer currentFarmer = farmerRepo.findById(((Farmer)user).getFarmerID()).orElse(null);
        if (currentFarmer != null) {
            currentFarmer.setName(farmerForm.getName());
            currentFarmer.setPhone(farmerForm.getPhone());
            currentFarmer.setAddress(farmerForm.getAddress());
            farmerRepo.save(currentFarmer);
            session.setAttribute("user", currentFarmer);
            redirectAttributes.addFlashAttribute("successMessage", "个人信息更新成功！");
        }
        return "redirect:/farmer/dashboard";
    }
}