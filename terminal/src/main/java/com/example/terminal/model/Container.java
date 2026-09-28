package com.example.terminal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String containerNumber;
    private String shippingLine;
    private String yardSlot;
    private String size;
    private String status;
    private String entryTime;
    private String pickupTime;
    private long dwellHours;
    private double demurrageAmount;
    @Transient
    private String dwellTimeDisplay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContainerNumber() {
        return containerNumber;
    }

    public void setContainerNumber(String containerNumber) {
        this.containerNumber = containerNumber;
    }

    public String getShippingLine() {
        return shippingLine;
    }

    public void setShippingLine(String shippingLine) {
        this.shippingLine = shippingLine;
    }

    public String getYardSlot() {
        return yardSlot;
    }

    public void setYardSlot(String yardSlot) {
        this.yardSlot = yardSlot;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public long getDwellHours() {
        return dwellHours;
    }

    public void setDwellHours(long dwellHours) {
        this.dwellHours = dwellHours;
    }

    public double getDemurrageAmount() {
        return demurrageAmount;
    }

    public void setDemurrageAmount(double demurrageAmount) {  
        this.demurrageAmount = demurrageAmount;
    }

    public String getDwellTimeDisplay() {
        return dwellTimeDisplay;
    }

    public void setDwellTimeDisplay(String dwellTimeDisplay) {
        this.dwellTimeDisplay = dwellTimeDisplay;
    }
}