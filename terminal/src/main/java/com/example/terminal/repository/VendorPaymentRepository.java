package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.VendorPayment;

public interface VendorPaymentRepository extends JpaRepository<VendorPayment, Long> {
}