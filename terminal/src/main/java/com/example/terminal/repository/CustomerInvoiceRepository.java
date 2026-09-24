package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.CustomerInvoice;

public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, Long> {
}