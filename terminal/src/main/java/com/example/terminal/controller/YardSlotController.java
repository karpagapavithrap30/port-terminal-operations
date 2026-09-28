package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.YardSlot;
import com.example.terminal.repository.YardSlotRepository;

@RestController
@RequestMapping("/api/port/slots")
public class YardSlotController {

    private final YardSlotRepository repository;

    public YardSlotController(YardSlotRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<YardSlot> getAllSlots() {
        return repository.findAll();
    }

    @PostMapping
    public YardSlot createSlot(@RequestBody YardSlot slot) {
        if (slot.getStatus() == null || slot.getStatus().trim().isEmpty()) {
            slot.setStatus("AVAILABLE");
        }
        return repository.save(slot);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public void deleteSlot(@org.springframework.web.bind.annotation.PathVariable Long id) {
        repository.deleteById(id);
    }
}