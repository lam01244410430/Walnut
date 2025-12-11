package com.walnut.app.repository;

import com.walnut.app.model.Consultation;
import com.walnut.app.model.Expert;
import com.walnut.app.model.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, String> {

    // --- HÀM BẠN ĐANG THIẾU ---
    // Tìm danh sách câu hỏi theo đối tượng Farmer
    List<Consultation> findByFarmer(Farmer farmer);
    // -------------------------

    // Các hàm khác phục vụ cho Expert
    List<Consultation> findByExpertAndAnswerIsNull(Expert expert);
    List<Consultation> findByExpertAndAnswerIsNotNull(Expert expert);
}