package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.JournalEntry;
import com.example.terminal.repository.JournalEntryRepository;

@RestController
@RequestMapping("/api/port/journal-entries")
public class JournalEntryController {

    private final JournalEntryRepository repository;

    public JournalEntryController(JournalEntryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<JournalEntry> getAllEntries() {
        return repository.findAll();
    }

    @PostMapping
    public JournalEntry createEntry(@RequestBody JournalEntry entry) {
        return repository.save(entry);
    }
}