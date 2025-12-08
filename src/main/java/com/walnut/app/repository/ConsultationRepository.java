package com.walnut.app.repository;

import com.walnut.app.model.Consultation;
import com.walnut.app.model.Expert;
import com.walnut.app.model.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, String> {

    // Cách 1: Tìm theo Object (Khuyên dùng)
    List<Consultation> findByFarmer(Farmer farmer);

    // Cách 2: Nếu muốn tìm theo ID String
    // Phải viết là findByFarmer_FarmerID vì trong Farmer biến tên là farmerID
    List<Consultation> findByFarmer_FarmerID(String farmerID);

    List<Consultation> findByExpertAndAnswerIsNull(Expert expert);
    List<Consultation> findByExpertAndAnswerIsNotNull(Expert expert);
}