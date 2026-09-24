package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.Container;
import com.example.terminal.repository.ContainerRepository;

@RestController
@RequestMapping("/api/port/containers")
public class ContainerController {

    private final ContainerRepository repository;

    public ContainerController(ContainerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Container> getAllContainers() {
        return repository.findAll();
    }

    @PostMapping
    public Container createContainer(@RequestBody Container container) {
        return repository.save(container);
    }

    @PutMapping("/{id}/pickup")
public Container pickupContainer(@PathVariable Long id) {

    Container container = repository.findById(id).orElseThrow();

    java.time.LocalDateTime pickupTime = java.time.LocalDateTime.now();
    java.time.LocalDateTime entryTime =
            java.time.LocalDateTime.parse(container.getEntryTime());

    long dwellHours = java.time.Duration.between(entryTime, pickupTime).toHours();

    double demurrageRatePerHour = 100.0;
    double demurrageAmount = 0.0;

    if (dwellHours > 48) {
        demurrageAmount = (dwellHours - 48) * demurrageRatePerHour;
    }

    container.setStatus("PICKED_UP");
    container.setPickupTime(pickupTime.toString());
    container.setDemurrageAmount(demurrageAmount);

    return repository.save(container);
}
}