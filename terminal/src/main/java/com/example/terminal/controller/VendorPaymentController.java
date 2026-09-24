package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.VendorBill;
import com.example.terminal.model.VendorPayment;
import com.example.terminal.repository.VendorBillRepository;
import com.example.terminal.repository.VendorPaymentRepository;

@RestController
@RequestMapping("/api/port/vendor-payments")
public class VendorPaymentController {

    private final VendorPaymentRepository paymentRepository;
    private final VendorBillRepository billRepository;

    public VendorPaymentController(
            VendorPaymentRepository paymentRepository,
            VendorBillRepository billRepository) {

        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
    }

    @GetMapping
    public List<VendorPayment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @PostMapping
    public VendorPayment createPayment(
            @RequestBody VendorPayment payment) {

        VendorBill bill = billRepository
                .findById(payment.getVendorBillId())
                .orElseThrow();

        payment.setAmount(bill.getAmount());
        payment.setVendorId(bill.getVendorId());
        payment.setStatus("PAID");

        bill.setStatus("PAID");
        billRepository.save(bill);

        return paymentRepository.save(payment);
    }
}