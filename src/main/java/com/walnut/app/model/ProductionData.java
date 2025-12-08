package com.walnut.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "production_data")
public class ProductionData {

    @Id
    @Column(name = "data_id", length = 20)
    private String dataID;

    @Column(name = "yield_val", nullable = false)
    private double yield; // Tổng sản lượng (kg)

    @Column(nullable = false)
    private double area;  // Diện tích (mẫu)

    @Column(nullable = false)
    private int year;     // Năm

    @Column(name = "record_date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    public ProductionData() {}

    // Constructor đầy đủ
    public ProductionData(String dataID, double yield, double area, int year, LocalDate date, Farmer farmer) {
        this.dataID = dataID;
        this.yield = yield;
        this.area = area;
        this.year = year;
        this.date = date;
        this.farmer = farmer;
    }

    // --- GETTERS & SETTERS ---
    public String getDataID() { return dataID; }
    public void setDataID(String dataID) { this.dataID = dataID; }

    public double getYield() { return yield; }
    public void setYield(double yield) { this.yield = yield; }

    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }
}