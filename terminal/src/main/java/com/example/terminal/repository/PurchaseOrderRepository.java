package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}