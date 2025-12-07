package com.walnut.app.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "farmer")
public class Farmer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // Tên đăng nhập
    private String password; // Mật khẩu
    private String name;     // Họ tên hiển thị (Tiếng Trung)

    // Một nông dân có nhiều dữ liệu sản xuất
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL)
    private List<ProductionData> productionDataList;

    public Farmer() {}
    public Farmer(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public List<ProductionData> getProductionDataList() { return productionDataList; }
}