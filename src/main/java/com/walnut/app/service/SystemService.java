package com.walnut.app.service;

import com.walnut.app.model.*;
import com.walnut.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SystemService {

    @Autowired private PestRepository pestRepo;
    @Autowired private FarmerRepository farmerRepo;


    public List<PestDisease> searchPest(String keyword) {
        return Collections.unmodifiableList(pestRepo.findBySymptomsContainingIgnoreCaseOrNameContainingIgnoreCase(keyword, keyword));
    }

    public double predictYield(Long farmerId) { // Tìm theo ID số (Long)
        Farmer f = (Farmer) farmerRepo.findById(farmerId).orElse(null);
        if (f == null || f.getProductionDataList() == null) return 0.0;

        double total = 0;
        for (ProductionData data : f.getProductionDataList()) {
            total += data.getYieldAmount();
        }
        if (f.getProductionDataList().isEmpty()) return 0.0;

        double avg = total / f.getProductionDataList().size();
        return avg * 1.1; // Dự báo tăng 10%
    }
}