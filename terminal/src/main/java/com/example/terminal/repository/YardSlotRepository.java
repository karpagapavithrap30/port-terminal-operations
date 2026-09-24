package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.YardSlot;

public interface YardSlotRepository extends JpaRepository<YardSlot, Long> {
}