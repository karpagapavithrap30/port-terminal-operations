package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.VendorBill;

public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {
}