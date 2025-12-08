package com.walnut.app.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expert")
public class Expert {

    @Id
    @Column(name = "expert_id", length = 20)
    private String expertID;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(unique = true, nullable = false)
    private String username; // Đây là Số điện thoại đăng nhập

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String specialty; // Chuyên môn

    private String email;

    // --- BỔ SUNG TRƯỜNG PHONE ĐỂ HẾT LỖI ---
    @Column(length = 20)
    private String phone;
    // ---------------------------------------

    // 1 Expert -> Nhiều Consultation
    @OneToMany(mappedBy = "expert", fetch = FetchType.LAZY)
    private List<Consultation> consultationList = new ArrayList<>();

    // --- CONSTRUCTORS ---
    public Expert() {}

    public Expert(String expertID, String name, String username, String password, String specialty, String phone) {
        this.expertID = expertID;
        this.name = name;
        this.username = username;
        this.password = password;
        this.specialty = specialty;
        this.phone = phone;
    }

    // --- GETTERS & SETTERS ---
    public String getExpertID() { return expertID; }
    public void setExpertID(String expertID) { this.expertID = expertID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // --- ĐÂY LÀ HÀM BẠN ĐANG THIẾU ---
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    // ---------------------------------

    public List<Consultation> getConsultationList() { return consultationList; }
    public void setConsultationList(List<Consultation> consultationList) { this.consultationList = consultationList; }

    public boolean matchSpecialty(String field) {
        return this.specialty != null && this.specialty.contains(field);
    }
}