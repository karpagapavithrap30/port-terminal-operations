package com.example.terminal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Crane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String craneCode;
    private String type;
    private String status;
    private String currentZone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCraneCode() {
        return craneCode;
    }

    public void setCraneCode(String craneCode) {
        this.craneCode = craneCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentZone() {
        return currentZone;
    }

    public void setCurrentZone(String currentZone) {
        this.currentZone = currentZone;
    }
}