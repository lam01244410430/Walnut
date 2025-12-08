package com.walnut.app.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "farmer")
public class Farmer {

    @Id
    @Column(name = "farmer_id", length = 20)
    private String farmerID; // Ví dụ: "FARMER-001"

    @Column(nullable = false, length = 20)
    private String name;

    @Column(unique = true, nullable = false)
    private String username; // Dùng để đăng nhập

    @Column(nullable = false)
    private String password; // Dùng để đăng nhập

    private String phone;
    private String address;

    // --- CÁC MỐI QUAN HỆ (RELATIONSHIPS) ---

    // 1 Farmer -> Nhiều ProductionData
    // mappedBy = "farmer" nghĩa là biến 'farmer' trong class ProductionData quản lý khóa ngoại
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductionData> productionDataList = new ArrayList<>();

    // 1 Farmer -> Nhiều Consultation
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Consultation> consultationList = new ArrayList<>();

    // --- CONSTRUCTORS ---
    public Farmer() {}

    public Farmer(String farmerID, String name, String username, String password) {
        this.farmerID = farmerID;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    // --- GETTERS & SETTERS ---
    public String getFarmerID() { return farmerID; }
    public void setFarmerID(String farmerID) { this.farmerID = farmerID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<ProductionData> getProductionDataList() { return productionDataList; }
    public void setProductionDataList(List<ProductionData> productionDataList) { this.productionDataList = productionDataList; }

    public List<Consultation> getConsultationList() { return consultationList; }
    public void setConsultationList(List<Consultation> consultationList) { this.consultationList = consultationList; }

    // Phương thức tiện ích để tính tổng sản lượng (Logic nghiệp vụ đơn giản)
    public double totalYield() {
        if (productionDataList == null || productionDataList.isEmpty()) return 0.0;
        double total = 0;
        for (ProductionData data : productionDataList) {
            total += data.getYield(); // Giả sử ProductionData có method getYield()
        }
        return total;
    }
}