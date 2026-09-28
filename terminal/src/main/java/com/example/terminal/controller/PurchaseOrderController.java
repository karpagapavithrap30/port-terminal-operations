package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.PurchaseOrder;
import com.example.terminal.repository.PurchaseOrderRepository;

@RestController
@RequestMapping("/api/port/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderRepository repository;

    public PurchaseOrderController(PurchaseOrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return repository.findAll();
    }

    @PostMapping
    public PurchaseOrder createPurchaseOrder(
            @RequestBody PurchaseOrder purchaseOrder) {

        double total =
                purchaseOrder.getQuantity()
                * purchaseOrder.getUnitPrice();

        purchaseOrder.setTotalAmount(total);

        if (purchaseOrder.getStatus() == null ||
            purchaseOrder.getStatus().isEmpty()) {
            purchaseOrder.setStatus("CREATED");
        }

        return repository.save(purchaseOrder);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public void deletePurchaseOrder(@org.springframework.web.bind.annotation.PathVariable Long id) {
        repository.deleteById(id);
    }
}