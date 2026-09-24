package com.example.terminal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.terminal.model.JournalEntry;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
}