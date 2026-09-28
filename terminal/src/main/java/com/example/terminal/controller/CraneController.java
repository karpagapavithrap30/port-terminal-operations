package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.Crane;
import com.example.terminal.repository.CraneRepository;

@RestController
@RequestMapping("/api/port/cranes")
public class CraneController {

    private final CraneRepository repository;

    public CraneController(CraneRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Crane> getAllCranes() {
        return repository.findAll();
    }

    @PostMapping
    public Crane createCrane(@RequestBody Crane crane) {
        if (crane.getStatus() == null || crane.getStatus().trim().isEmpty()) {
            crane.setStatus("AVAILABLE");
        }
        return repository.save(crane);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public void deleteCrane(@org.springframework.web.bind.annotation.PathVariable Long id) {
        repository.deleteById(id);
    }
}