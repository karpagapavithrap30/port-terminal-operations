package com.example.terminal.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.terminal.model.Container;
import com.example.terminal.model.JournalEntry;
import com.example.terminal.model.YardSlot;
import com.example.terminal.repository.ContainerRepository;
import com.example.terminal.repository.JournalEntryRepository;
import com.example.terminal.repository.ShippingLineRepository;
import com.example.terminal.repository.YardSlotRepository;

@RestController
@RequestMapping("/api/port/containers")
public class ContainerController {

    private static final long FREE_STORAGE_HOURS = 48;
    private static final double DEMURRAGE_RATE_PER_HOUR = 100.0;

    private final ContainerRepository repository;
    private final YardSlotRepository slotRepository;
    private final JournalEntryRepository journalRepository;
    private final ShippingLineRepository shippingLineRepository;

    public ContainerController(ContainerRepository repository,
                               YardSlotRepository slotRepository,
                               JournalEntryRepository journalRepository,
                               ShippingLineRepository shippingLineRepository) {
        this.repository = repository;
        this.slotRepository = slotRepository;
        this.journalRepository = journalRepository;
        this.shippingLineRepository = shippingLineRepository;
    }

    @GetMapping
    public List<Container> getAllContainers() {
        List<Container> containers = repository.findAll();
        containers.forEach(container -> calculateCurrentValues(container, LocalDateTime.now()));
        return containers;
    }

    @PostMapping
    public Container createContainer(@RequestBody Container container) {
        String containerNumber = normalizeRequired(container.getContainerNumber(), "Container number");
        if (repository.existsByContainerNumberIgnoreCase(containerNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Container number already exists. Please enter a unique container number.");
        }
        container.setContainerNumber(containerNumber);

        String shippingLine = normalizeRequired(container.getShippingLine(), "Shipping line");
        if (!shippingLineRepository.existsByNameIgnoreCase(shippingLine)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shipping line is invalid");
        }
        container.setShippingLine(shippingLine);

        String yardSlot = normalizeOptional(container.getYardSlot());
        if (yardSlot != null) {
            if (!yardSlot.matches("[A-Z][0-9]{2}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yard slot must use a format such as A01 or B02");
            }
            YardSlot slot = slotRepository.findBySlotCode(yardSlot).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yard slot does not exist"));
            container.setYardSlot(yardSlot);
            slot.setStatus("OCCUPIED");
            slotRepository.save(slot);
        } else {
            container.setYardSlot(null);
        }

        LocalDateTime entryTime = parseDateTime(container.getEntryTime(), "Entry time");
        container.setEntryTime(entryTime.toString());
        container.setPickupTime(null);
        container.setStatus("IN_YARD");
        container.setDwellHours(0);
        container.setDemurrageAmount(0);

        return repository.save(container);
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeOptional(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    @PutMapping("/{id}/pickup")
    public Container pickupContainer(@PathVariable Long id) {
        Container container = repository.findById(id).orElseThrow(() -> new RuntimeException("Container not found: " + id));

        LocalDateTime pickupTime = LocalDateTime.now();
        LocalDateTime entryTime = parseDateTime(container.getEntryTime(), "Entry time");
        Duration dwell = Duration.between(entryTime, pickupTime);
        if (dwell.isNegative()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pickup time cannot be earlier than entry time");
        }
        double demurrageAmount = calculateDemurrage(dwell);
        long dwellHours = dwell.toHours();

        container.setStatus("PICKED_UP");
        container.setPickupTime(pickupTime.toString());
        container.setDwellHours(dwellHours);
        container.setDemurrageAmount(demurrageAmount);
        container.setDwellTimeDisplay(formatDwellTime(dwell));

        // Free up yard slot if allocated
        if (container.getYardSlot() != null && !container.getYardSlot().trim().isEmpty()) {
            Optional<YardSlot> slotOpt = slotRepository.findBySlotCode(container.getYardSlot());
            if (slotOpt.isPresent()) {
                YardSlot slot = slotOpt.get();
                slot.setStatus("AVAILABLE");
                slotRepository.save(slot);
            }
        }

        // Generate journal entry if demurrage was charged
        if (demurrageAmount > 0) {
            JournalEntry entry = new JournalEntry();
            entry.setEntryType("DEMURRAGE");
            entry.setAccountName("Demurrage Revenue");
            entry.setDebit(0);
            entry.setCredit(demurrageAmount);
            entry.setDescription("Demurrage charged for Container " + container.getContainerNumber() + " (" + dwellHours + " hrs dwell)");
            journalRepository.save(entry);

            JournalEntry recEntry = new JournalEntry();
            recEntry.setEntryType("DEMURRAGE");
            recEntry.setAccountName("Customer Receivable");
            recEntry.setDebit(demurrageAmount);
            recEntry.setCredit(0);
            recEntry.setDescription("Demurrage receivable for Container " + container.getContainerNumber());
            journalRepository.save(recEntry);
        }

        return repository.save(container);
    }

    private void calculateCurrentValues(Container container, LocalDateTime now) {
        LocalDateTime entryTime = parseDateTime(container.getEntryTime(), "Entry time");
        LocalDateTime endTime = "PICKED_UP".equals(container.getStatus())
                ? parseDateTime(container.getPickupTime(), "Pickup time") : now;
        Duration dwell = Duration.between(entryTime, endTime);
        if (dwell.isNegative()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Dwell time cannot be negative");
        }
        container.setDwellHours(dwell.toHours());
        container.setDemurrageAmount(calculateDemurrage(dwell));
        container.setDwellTimeDisplay(formatDwellTime(dwell));
    }

    static double calculateDemurrage(Duration dwell) {
        Duration excess = dwell.minusHours(FREE_STORAGE_HOURS);
        if (excess.isNegative() || excess.isZero()) {
            return 0;
        }
        return Math.max(0, excess.toMillis() / 3_600_000.0 * DEMURRAGE_RATE_PER_HOUR);
    }

    static String formatDwellTime(Duration dwell) {
        long totalSeconds = dwell.getSeconds();
        if (totalSeconds < 60) {
            return totalSeconds + " sec";
        }
        long totalMinutes = totalSeconds / 60;
        if (totalMinutes < 60) {
            long seconds = totalSeconds % 60;
            return totalMinutes + " min" + (seconds == 0 ? "" : " " + seconds + " sec");
        }
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        long days = hours / 24;
        long remainingHours = hours % 24;
        if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s")
                    + (remainingHours == 0 ? "" : " " + remainingHours + " hr" + (remainingHours == 1 ? "" : "s"))
                    + (minutes == 0 ? "" : " " + minutes + " min");
        }
        return hours + " hr" + (hours == 1 ? "" : "s") + (minutes == 0 ? "" : " " + minutes + " min");
    }

    private LocalDateTime parseDateTime(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " has an invalid format", exception);
        }
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public void deleteContainer(@PathVariable Long id) {
        repository.deleteById(id);
    }
}