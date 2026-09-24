package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.ShippingLine;

public interface ShippingLineRepository extends JpaRepository<ShippingLine, Long> {
}