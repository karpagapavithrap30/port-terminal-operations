package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.AnalyticAccount;
import com.example.terminal.repository.AnalyticAccountRepository;

@RestController
@RequestMapping("/api/port/analytic-accounts")
public class AnalyticAccountController {

    private final AnalyticAccountRepository repository;

    public AnalyticAccountController(AnalyticAccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AnalyticAccount> getAllAccounts() {
        return repository.findAll();
    }

    @PostMapping
    public AnalyticAccount createAccount(
            @RequestBody AnalyticAccount account) {
        return repository.save(account);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public void deleteAccount(@org.springframework.web.bind.annotation.PathVariable Long id) {
        repository.deleteById(id);
    }
}