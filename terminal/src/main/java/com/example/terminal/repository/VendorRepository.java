package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}