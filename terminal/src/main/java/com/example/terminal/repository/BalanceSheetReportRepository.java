package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.BalanceSheetReport;

public interface BalanceSheetReportRepository extends JpaRepository<BalanceSheetReport, Long> {
}