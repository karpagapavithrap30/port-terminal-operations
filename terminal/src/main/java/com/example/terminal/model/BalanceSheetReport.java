package com.example.terminal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BalanceSheetReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalAssets;
    private double totalLiabilities;
    private double totalEquity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(double totalAssets) {
        this.totalAssets = totalAssets;
    }

    public double getTotalLiabilities() {
        return totalLiabilities;
    }

    public void setTotalLiabilities(double totalLiabilities) {
        this.totalLiabilities = totalLiabilities;
    }

    public double getTotalEquity() {
        return totalEquity;
    }

    public void setTotalEquity(double totalEquity) {
        this.totalEquity = totalEquity;
    }
}