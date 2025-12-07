package com.walnut.app.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "production_data")
public class ProductionData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double yieldAmount;
    private Date recordDate;

    @ManyToOne
    @JoinColumn(name = "farmer_id") // Khóa ngoại trỏ về bảng Farmer
    private Farmer farmer;

    public ProductionData() {}
    // Getters...
    public Double getYieldAmount() { return yieldAmount; }
}