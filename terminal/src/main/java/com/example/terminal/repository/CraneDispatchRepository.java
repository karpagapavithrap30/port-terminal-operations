package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.CraneDispatch;

public interface CraneDispatchRepository extends JpaRepository<CraneDispatch, Long> {
}