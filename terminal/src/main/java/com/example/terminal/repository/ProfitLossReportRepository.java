package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.ProfitLossReport;

public interface ProfitLossReportRepository extends JpaRepository<ProfitLossReport, Long> {
}