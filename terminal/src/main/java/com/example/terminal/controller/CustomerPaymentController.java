package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.CustomerInvoice;
import com.example.terminal.model.CustomerPayment;
import com.example.terminal.repository.CustomerInvoiceRepository;
import com.example.terminal.repository.CustomerPaymentRepository;

@RestController
@RequestMapping("/api/port/customer-payments")
public class CustomerPaymentController {

    private final CustomerPaymentRepository paymentRepository;
    private final CustomerInvoiceRepository invoiceRepository;

    public CustomerPaymentController(
            CustomerPaymentRepository paymentRepository,
            CustomerInvoiceRepository invoiceRepository) {

        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
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
                .orElseThrow();

        payment.setAmount(invoice.getTotalAmount());
        payment.setShippingLineId(invoice.getShippingLineId());
        payment.setStatus("PAID");

        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);

        return paymentRepository.save(payment);
    }
}