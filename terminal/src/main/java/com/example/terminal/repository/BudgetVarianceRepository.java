package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.BudgetVariance;

public interface BudgetVarianceRepository extends JpaRepository<BudgetVariance, Long> {
}