package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.ShippingLine;
import com.example.terminal.repository.ShippingLineRepository;

@RestController
@RequestMapping({"/api/shipping-lines", "/api/port/shipping-lines"})
public class ShippingLineController {

    private final ShippingLineRepository repository;

    public ShippingLineController(ShippingLineRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ShippingLine> getAllShippingLines() {
        return repository.findAll();
    }

    @PostMapping
    public ShippingLine createShippingLine(@RequestBody ShippingLine shippingLine) {
        return repository.save(shippingLine);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public void deleteShippingLine(@org.springframework.web.bind.annotation.PathVariable Long id) {
        repository.deleteById(id);
    }
}