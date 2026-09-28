package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.JournalEntry;
import com.example.terminal.model.VendorBill;
import com.example.terminal.repository.JournalEntryRepository;
import com.example.terminal.repository.VendorBillRepository;

@RestController
@RequestMapping("/api/port/vendor-bills")
public class VendorBillController {

    private final VendorBillRepository repository;
    private final JournalEntryRepository journalRepository;

    public VendorBillController(VendorBillRepository repository, JournalEntryRepository journalRepository) {
        this.repository = repository;
        this.journalRepository = journalRepository;
    }

    @GetMapping
    public List<VendorBill> getAllVendorBills() {
        return repository.findAll();
    }

    @PostMapping
    public VendorBill createVendorBill(
            @RequestBody VendorBill vendorBill) {

        if (vendorBill.getStatus() == null ||
            vendorBill.getStatus().isEmpty()) {
            vendorBill.setStatus("UNPAID");
        }

        VendorBill saved = repository.save(vendorBill);

        // Create Journal Entries (Expense Debit, Vendor Payable Credit)
        JournalEntry expEntry = new JournalEntry();
        expEntry.setEntryType("PURCHASE");
        expEntry.setAccountName("Fuel Expense");
        expEntry.setDebit(saved.getAmount());
        expEntry.setCredit(0);
        expEntry.setDescription("Vendor Bill #" + saved.getId() + " - " + saved.getItem());
        journalRepository.save(expEntry);

        JournalEntry payEntry = new JournalEntry();
        payEntry.setEntryType("PURCHASE");
        payEntry.setAccountName("Vendor Payable");
        payEntry.setDebit(0);
        payEntry.setCredit(saved.getAmount());
        payEntry.setDescription("Vendor Bill Payable #" + saved.getId());
        journalRepository.save(payEntry);

        return saved;
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public void deleteVendorBill(@org.springframework.web.bind.annotation.PathVariable Long id) {
        repository.deleteById(id);
    }
}