package com.walnut.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "expert") // Tên bảng: 专家表 (Bảng Chuyên gia)
public class Expert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 编号 (Mã số)

    @Column(nullable = false, unique = true)
    private String username; // 用户名 (Tên đăng nhập)

    @Column(nullable = false)
    private String password; // 密码 (Mật khẩu)

    @Column(nullable = false)
    private String name; // 姓名 (Họ tên hiển thị - VD: 李教授)

    private String specialization; // 专业领域 (Chuyên môn - VD: 病虫害防治)

    // --- Constructors (构造函数) ---

    public Expert() {}

    public Expert(String username, String password, String name, String specialization) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.specialization = specialization;
    }

    // --- Getters and Setters (获取和设置方法) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}