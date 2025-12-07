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
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    public ProductionData() {}

    public ProductionData(Double yieldAmount, Date recordDate, Farmer farmer) {
        this.yieldAmount = yieldAmount;
        this.recordDate = recordDate;
        this.farmer = farmer;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public Double getYieldAmount() { return yieldAmount; }
    public void setYieldAmount(Double yieldAmount) { this.yieldAmount = yieldAmount; }
    public Date getRecordDate() { return recordDate; }
    public void setRecordDate(Date recordDate) { this.recordDate = recordDate; }
    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }
}