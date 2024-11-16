package com.example.laptoprecommendationsystem.model;

import jakarta.persistence.*;

@Entity
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String brand;
    @Column
    private String usageType;
    @Column
    private Double budgetMin;
    @Column
    private Double budgetMax;
    @Column
    private String memory;
    @Column
    private String performance;
    @Column
    private String screenSize;
    @Column
    private String storage;

    // Getters and setters

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public Double getBudgetMin() {
        return budgetMin;
    }

    public void setBudgetMin(Double budgetMin) {
        this.budgetMin = budgetMin;
    }

    public Double getBudgetMax() {
        return budgetMax;
    }

    public void setBudgetMax(Double budgetMax) {
        this.budgetMax = budgetMax;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }
}
