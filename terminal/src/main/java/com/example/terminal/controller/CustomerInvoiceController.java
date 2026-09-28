package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.CustomerInvoice;
import com.example.terminal.model.JournalEntry;
import com.example.terminal.model.SalesOrder;
import com.example.terminal.repository.CustomerInvoiceRepository;
import com.example.terminal.repository.JournalEntryRepository;
import com.example.terminal.repository.SalesOrderRepository;

@RestController
@RequestMapping("/api/port/customer-invoices")
public class CustomerInvoiceController {

    private final CustomerInvoiceRepository invoiceRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final JournalEntryRepository journalRepository;

    public CustomerInvoiceController(
            CustomerInvoiceRepository invoiceRepository,
            SalesOrderRepository salesOrderRepository,
            JournalEntryRepository journalRepository) {

        this.invoiceRepository = invoiceRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.journalRepository = journalRepository;
    }

    @GetMapping
    public List<CustomerInvoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @PostMapping
    public CustomerInvoice createInvoice(
            @RequestBody CustomerInvoice invoice) {

        if (invoice.getSalesOrderId() != null) {
            SalesOrder salesOrder = salesOrderRepository
                    .findById(invoice.getSalesOrderId())
                    .orElse(null);
            if (salesOrder != null) {
                invoice.setShippingLineId(salesOrder.getShippingLineId());
                invoice.setHandlingAmount(salesOrder.getTotalAmount());
            }
        }

        double total =
                invoice.getHandlingAmount()
                + invoice.getDemurrageAmount();

        invoice.setTotalAmount(total);

        if (invoice.getStatus() == null ||
            invoice.getStatus().isEmpty()) {
            invoice.setStatus("UNPAID");
        }

        CustomerInvoice saved = invoiceRepository.save(invoice);

        // Record Journal Entry
        if (saved.getHandlingAmount() > 0) {
            JournalEntry hRev = new JournalEntry();
            hRev.setEntryType("SALES_INVOICE");
            hRev.setAccountName("Terminal Handling Revenue");
            hRev.setDebit(0);
            hRev.setCredit(saved.getHandlingAmount());
            hRev.setDescription("Invoice #" + saved.getId() + " Terminal Handling Fee");
            journalRepository.save(hRev);
        }

        if (saved.getDemurrageAmount() > 0) {
            JournalEntry dRev = new JournalEntry();
            dRev.setEntryType("SALES_INVOICE");
            dRev.setAccountName("Demurrage Revenue");
            dRev.setDebit(0);
            dRev.setCredit(saved.getDemurrageAmount());
            dRev.setDescription("Invoice #" + saved.getId() + " Demurrage Fee");
            journalRepository.save(dRev);
        }

        JournalEntry rec = new JournalEntry();
        rec.setEntryType("SALES_INVOICE");
        rec.setAccountName("Customer Receivable");
        rec.setDebit(saved.getTotalAmount());
        rec.setCredit(0);
        rec.setDescription("Customer Invoice #" + saved.getId() + " Receivable");
        journalRepository.save(rec);

        return saved;
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public void deleteInvoice(@org.springframework.web.bind.annotation.PathVariable Long id) {
        invoiceRepository.deleteById(id);
    }
}