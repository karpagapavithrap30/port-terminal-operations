package com.example.terminal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.Crane;

public interface CraneRepository extends JpaRepository<Crane, Long> {

    List<Crane> findByStatus(String status);
}