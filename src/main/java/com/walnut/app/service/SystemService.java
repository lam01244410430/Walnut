package com.walnut.app.service;

import com.walnut.app.model.*;
import com.walnut.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SystemService {

    @Autowired private PestRepository pestRepo;
    @Autowired private FarmerRepository farmerRepo;

    // Chức năng 1: Tìm kiếm bệnh hại
    public List<PestDisease> searchPest(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // Gọi hàm đã định nghĩa trong PestRepository ở Bước 1
        return pestRepo.findBySymptomsContainingIgnoreCaseOrNameContainingIgnoreCase(keyword, keyword);
    }

    // Chức năng 2: Dự đoán sản lượng
    @Transactional(readOnly = true) // Quan trọng để Lazy Loading hoạt động tốt
    public double predictYield(String farmerId) {
        // Tìm Farmer, nếu không thấy thì trả về null
        Farmer f = farmerRepo.findById(farmerId).orElse(null);

        // Kiểm tra null và kiểm tra list rỗng
        if (f == null || f.getProductionDataList() == null || f.getProductionDataList().isEmpty()) {
            return 0.0;
        }

        double total = 0;
        // Duyệt qua danh sách đã được định nghĩa trong Farmer ở Bước 3
        for (ProductionData data : f.getProductionDataList()) {
            total += data.getYield();
        }

        // Tính trung bình
        double avg = total / f.getProductionDataList().size();

        // Dự đoán tăng 10% cho năm sau (Logic ví dụ)
        return avg * 1.1;
    }
}