package com.walnut.app.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @Column(name = "admin_id", length = 20)
    private String adminID; // Ví dụ: "ADMIN-001"

    @Column(nullable = false, length = 20)
    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // --- CÁC MỐI QUAN HỆ ---

    // 1 Admin -> Nhiều TrainingMaterial
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrainingMaterial> trainingMaterialList = new ArrayList<>();

    // --- CONSTRUCTORS ---
    public Admin() {}

    public Admin(String adminID, String name, String username, String password) {
        this.adminID = adminID;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    // --- GETTERS & SETTERS ---
    public String getAdminID() { return adminID; }
    public void setAdminID(String adminID) { this.adminID = adminID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<TrainingMaterial> getTrainingMaterialList() { return trainingMaterialList; }
    public void setTrainingMaterialList(List<TrainingMaterial> trainingMaterialList) { this.trainingMaterialList = trainingMaterialList; }
}