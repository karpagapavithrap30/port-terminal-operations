package com.example.terminal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BudgetVariance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountName;
    private double budgetAmount;
    private double actualAmount;
    private double varianceAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public double getVarianceAmount() {
        return varianceAmount;
    }

    public void setVarianceAmount(double varianceAmount) {
        this.varianceAmount = varianceAmount;
    }
}