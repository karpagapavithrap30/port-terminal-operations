package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.CustomerInvoice;
import com.example.terminal.model.SalesOrder;
import com.example.terminal.repository.CustomerInvoiceRepository;
import com.example.terminal.repository.SalesOrderRepository;

@RestController
@RequestMapping("/api/port/customer-invoices")
public class CustomerInvoiceController {

    private final CustomerInvoiceRepository invoiceRepository;
    private final SalesOrderRepository salesOrderRepository;

    public CustomerInvoiceController(
            CustomerInvoiceRepository invoiceRepository,
            SalesOrderRepository salesOrderRepository) {

        this.invoiceRepository = invoiceRepository;
        this.salesOrderRepository = salesOrderRepository;
    }

    @GetMapping
    public List<CustomerInvoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @PostMapping
    public CustomerInvoice createInvoice(
            @RequestBody CustomerInvoice invoice) {

        SalesOrder salesOrder = salesOrderRepository
                .findById(invoice.getSalesOrderId())
                .orElseThrow();

        invoice.setShippingLineId(salesOrder.getShippingLineId());
        invoice.setHandlingAmount(salesOrder.getTotalAmount());

        double total =
                invoice.getHandlingAmount()
                + invoice.getDemurrageAmount();

        invoice.setTotalAmount(total);

        if (invoice.getStatus() == null ||
            invoice.getStatus().isEmpty()) {
            invoice.setStatus("UNPAID");
        }

        return invoiceRepository.save(invoice);
    }
}