package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.AnalyticAccount;
import com.example.terminal.model.JournalEntry;
import com.example.terminal.model.VendorBill;
import com.example.terminal.model.VendorPayment;
import com.example.terminal.repository.AnalyticAccountRepository;
import com.example.terminal.repository.JournalEntryRepository;
import com.example.terminal.repository.VendorBillRepository;
import com.example.terminal.repository.VendorPaymentRepository;

@RestController
@RequestMapping("/api/port/vendor-payments")
public class VendorPaymentController {

    private final VendorPaymentRepository paymentRepository;
    private final VendorBillRepository billRepository;
    private final JournalEntryRepository journalRepository;
    private final AnalyticAccountRepository analyticRepository;

    public VendorPaymentController(
            VendorPaymentRepository paymentRepository,
            VendorBillRepository billRepository,
            JournalEntryRepository journalRepository,
            AnalyticAccountRepository analyticRepository) {

        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
        this.journalRepository = journalRepository;
        this.analyticRepository = analyticRepository;
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
                .orElseThrow(() -> new RuntimeException("Vendor bill not found: " + payment.getVendorBillId()));

        payment.setAmount(bill.getAmount());
        payment.setVendorId(bill.getVendorId());
        payment.setStatus("PAID");

        bill.setStatus("PAID");
        billRepository.save(bill);

        VendorPayment saved = paymentRepository.save(payment);

        // Journal Entries: Debit Vendor Payable, Credit Bank
        JournalEntry debEntry = new JournalEntry();
        debEntry.setEntryType("VENDOR_PAYMENT");
        debEntry.setAccountName("Vendor Payable");
        debEntry.setDebit(saved.getAmount());
        debEntry.setCredit(0);
        debEntry.setDescription("Vendor Payment #" + saved.getId() + " for Bill #" + bill.getId());
        journalRepository.save(debEntry);

        JournalEntry credEntry = new JournalEntry();
        credEntry.setEntryType("VENDOR_PAYMENT");
        credEntry.setAccountName("Bank");
        credEntry.setDebit(0);
        credEntry.setCredit(saved.getAmount());
        credEntry.setDescription("Bank payout for Vendor Payment #" + saved.getId());
        journalRepository.save(credEntry);

        // Update analytic accounts actual spend
        List<AnalyticAccount> analytics = analyticRepository.findAll();
        for (AnalyticAccount acc : analytics) {
            acc.setActualAmount(acc.getActualAmount() + saved.getAmount());
            analyticRepository.save(acc);
        }

        return saved;
    }
}