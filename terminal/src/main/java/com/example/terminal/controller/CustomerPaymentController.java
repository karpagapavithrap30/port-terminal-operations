package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.CustomerInvoice;
import com.example.terminal.model.CustomerPayment;
import com.example.terminal.model.JournalEntry;
import com.example.terminal.repository.CustomerInvoiceRepository;
import com.example.terminal.repository.CustomerPaymentRepository;
import com.example.terminal.repository.JournalEntryRepository;

@RestController
@RequestMapping("/api/port/customer-payments")
public class CustomerPaymentController {

    private final CustomerPaymentRepository paymentRepository;
    private final CustomerInvoiceRepository invoiceRepository;
    private final JournalEntryRepository journalRepository;

    public CustomerPaymentController(
            CustomerPaymentRepository paymentRepository,
            CustomerInvoiceRepository invoiceRepository,
            JournalEntryRepository journalRepository) {

        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.journalRepository = journalRepository;
    }

    @GetMapping
    public List<CustomerPayment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @PostMapping
    public CustomerPayment createPayment(
            @RequestBody CustomerPayment payment) {

        CustomerInvoice invoice = invoiceRepository
                .findById(payment.getCustomerInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + payment.getCustomerInvoiceId()));

        payment.setAmount(invoice.getTotalAmount());
        payment.setShippingLineId(invoice.getShippingLineId());
        payment.setStatus("PAID");

        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);

        CustomerPayment saved = paymentRepository.save(payment);

        // Record Journal Entries: Debit Bank, Credit Customer Receivable
        JournalEntry bankEntry = new JournalEntry();
        bankEntry.setEntryType("CUSTOMER_PAYMENT");
        bankEntry.setAccountName("Bank");
        bankEntry.setDebit(saved.getAmount());
        bankEntry.setCredit(0);
        bankEntry.setDescription("Bank receipt for Customer Payment #" + saved.getId());
        journalRepository.save(bankEntry);

        JournalEntry recEntry = new JournalEntry();
        recEntry.setEntryType("CUSTOMER_PAYMENT");
        recEntry.setAccountName("Customer Receivable");
        recEntry.setDebit(0);
        recEntry.setCredit(saved.getAmount());
        recEntry.setDescription("Clear Customer Receivable for Invoice #" + invoice.getId());
        journalRepository.save(recEntry);

        return saved;
    }
}