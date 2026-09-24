package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.SalesOrder;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
}