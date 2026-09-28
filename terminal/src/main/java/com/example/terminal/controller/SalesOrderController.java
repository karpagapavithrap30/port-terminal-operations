package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.SalesOrder;
import com.example.terminal.repository.SalesOrderRepository;

@RestController
@RequestMapping("/api/port/sales-orders")
public class SalesOrderController {

    private final SalesOrderRepository repository;

    public SalesOrderController(SalesOrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SalesOrder> getAllSalesOrders() {
        return repository.findAll();
    }

    @PostMapping
    public SalesOrder createSalesOrder(
            @RequestBody SalesOrder salesOrder) {

        double total =
                salesOrder.getQuantity()
                * salesOrder.getUnitPrice();

        salesOrder.setTotalAmount(total);

        if (salesOrder.getStatus() == null ||
            salesOrder.getStatus().isEmpty()) {
            salesOrder.setStatus("CREATED");
        }

        return repository.save(salesOrder);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public void deleteSalesOrder(@org.springframework.web.bind.annotation.PathVariable Long id) {
        repository.deleteById(id);
    }
}