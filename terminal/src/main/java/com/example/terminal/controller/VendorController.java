package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.Vendor;
import com.example.terminal.repository.VendorRepository;

@RestController
@RequestMapping({"/api/vendors", "/api/port/vendors"})
public class VendorController {

    private final VendorRepository repository;

    public VendorController(VendorRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Vendor> getAllVendors() {
        return repository.findAll();
    }

    @PostMapping
    public Vendor createVendor(@RequestBody Vendor vendor) {
        return repository.save(vendor);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public void deleteVendor(@org.springframework.web.bind.annotation.PathVariable Long id) {
        repository.deleteById(id);
    }
}