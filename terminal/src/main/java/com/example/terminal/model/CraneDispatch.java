package com.example.terminal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CraneDispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long containerId;
    private Long craneId;
    private Long yardSlotId;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public Long getCraneId() {
        return craneId;
    }

    public void setCraneId(Long craneId) {
        this.craneId = craneId;
    }

    public Long getYardSlotId() {
        return yardSlotId;
    }

    public void setYardSlotId(Long yardSlotId) {
        this.yardSlotId = yardSlotId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}