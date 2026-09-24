package com.example.terminal.controller;

import com.example.terminal.model.Crane;
import com.example.terminal.model.CraneDispatch;
import com.example.terminal.repository.CraneDispatchRepository;
import com.example.terminal.repository.CraneRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/port/crane-dispatch")
public class CraneDispatchController {

    private final CraneDispatchRepository dispatchRepository;
    private final CraneRepository craneRepository;

    public CraneDispatchController(
            CraneDispatchRepository dispatchRepository,
            CraneRepository craneRepository) {

        this.dispatchRepository = dispatchRepository;
        this.craneRepository = craneRepository;
    }

    @GetMapping
    public List<CraneDispatch> getAllDispatches() {
        return dispatchRepository.findAll();
    }

    @PostMapping
    public CraneDispatch createAutomaticDispatch(
            @RequestBody CraneDispatch dispatch) {

        List<Crane> availableCranes =
                craneRepository.findByStatus("AVAILABLE");

        if (availableCranes.isEmpty()) {
            throw new RuntimeException("No available crane found");
        }

        Crane selectedCrane = availableCranes.get(0);

        dispatch.setCraneId(selectedCrane.getId());
        dispatch.setStatus("ASSIGNED");

        selectedCrane.setStatus("BUSY");
        craneRepository.save(selectedCrane);

        return dispatchRepository.save(dispatch);
    }
}