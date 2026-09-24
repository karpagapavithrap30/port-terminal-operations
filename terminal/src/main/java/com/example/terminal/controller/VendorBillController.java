package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.VendorBill;
import com.example.terminal.repository.VendorBillRepository;

@RestController
@RequestMapping("/api/port/vendor-bills")
public class VendorBillController {

    private final VendorBillRepository repository;

    public VendorBillController(VendorBillRepository repository) {
        this.repository = repository;
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

        return repository.save(vendorBill);
    }
}