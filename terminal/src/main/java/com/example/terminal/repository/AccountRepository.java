package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}