package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.Account;
import com.example.terminal.repository.AccountRepository;

@RestController
@RequestMapping("/api/port/accounts")
public class AccountController {

    private final AccountRepository repository;

    public AccountController(AccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return repository.save(account);
    }
}