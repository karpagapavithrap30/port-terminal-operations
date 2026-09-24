package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.AnalyticAccount;

public interface AnalyticAccountRepository extends JpaRepository<AnalyticAccount, Long> {
}