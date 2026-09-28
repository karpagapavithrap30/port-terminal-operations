package com.example.terminal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.YardSlot;

public interface YardSlotRepository extends JpaRepository<YardSlot, Long> {
    List<YardSlot> findByStatus(String status);
    Optional<YardSlot> findBySlotCode(String slotCode);
}