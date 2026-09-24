package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.CustomerPayment;

public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {
}