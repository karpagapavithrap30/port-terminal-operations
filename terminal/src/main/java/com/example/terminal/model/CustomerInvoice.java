 package com.example.terminal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CustomerInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long salesOrderId;
    private Long shippingLineId;
    private double handlingAmount;
    private double demurrageAmount;
    private double totalAmount;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSalesOrderId() {
        return salesOrderId;
    }

    public void setSalesOrderId(Long salesOrderId) {
        this.salesOrderId = salesOrderId;
    }

    public Long getShippingLineId() {
        return shippingLineId;
    }

    public void setShippingLineId(Long shippingLineId) {
        this.shippingLineId = shippingLineId;
    }

    public double getHandlingAmount() {
        return handlingAmount;
    }

    public void setHandlingAmount(double handlingAmount) {
        this.handlingAmount = handlingAmount;
    }

    public double getDemurrageAmount() {
        return demurrageAmount;
    }

    public void setDemurrageAmount(double demurrageAmount) {
        this.demurrageAmount = demurrageAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
