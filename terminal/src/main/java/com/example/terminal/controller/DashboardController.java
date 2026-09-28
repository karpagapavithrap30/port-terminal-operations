package com.example.terminal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.AnalyticAccount;
import com.example.terminal.model.CustomerInvoice;
import com.example.terminal.model.JournalEntry;
import com.example.terminal.repository.AnalyticAccountRepository;
import com.example.terminal.repository.ContainerRepository;
import com.example.terminal.repository.CraneRepository;
import com.example.terminal.repository.CustomerInvoiceRepository;
import com.example.terminal.repository.JournalEntryRepository;
import com.example.terminal.repository.YardSlotRepository;

@RestController
@RequestMapping("/api/port/dashboard")
public class DashboardController {

    private final ContainerRepository containerRepository;
    private final YardSlotRepository slotRepository;
    private final CraneRepository craneRepository;
    private final CustomerInvoiceRepository invoiceRepository;
    private final JournalEntryRepository journalRepository;
    private final AnalyticAccountRepository analyticRepository;

    public DashboardController(ContainerRepository containerRepository,
                               YardSlotRepository slotRepository,
                               CraneRepository craneRepository,
                               CustomerInvoiceRepository invoiceRepository,
                               JournalEntryRepository journalRepository,
                               AnalyticAccountRepository analyticRepository) {
        this.containerRepository = containerRepository;
        this.slotRepository = slotRepository;
        this.craneRepository = craneRepository;
        this.invoiceRepository = invoiceRepository;
        this.journalRepository = journalRepository;
        this.analyticRepository = analyticRepository;
    }

    @GetMapping
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalContainers = containerRepository.count();
        long availableSlots = slotRepository.findByStatus("AVAILABLE").size();
        long availableCranes = craneRepository.findByStatus("AVAILABLE").size();
        
        List<CustomerInvoice> invoices = invoiceRepository.findAll();
        long pendingInvoices = invoices.stream()
                .filter(i -> "UNPAID".equalsIgnoreCase(i.getStatus()))
                .count();

        List<JournalEntry> entries = journalRepository.findAll();
        double totalRevenue = 0;
        double totalExpense = 0;

        for (JournalEntry entry : entries) {
            String acc = entry.getAccountName();
            if (acc == null) continue;

            if (acc.equalsIgnoreCase("Terminal Handling Revenue") || acc.equalsIgnoreCase("Demurrage Revenue")) {
                totalRevenue += entry.getCredit();
            } else if (acc.equalsIgnoreCase("Fuel Expense") || acc.equalsIgnoreCase("Maintenance Expense") || acc.equalsIgnoreCase("Vendor Expense")) {
                totalExpense += entry.getDebit();
            }
        }

        double netProfit = totalRevenue - totalExpense;

        List<AnalyticAccount> analytics = analyticRepository.findAll();
        double totalBudgetVariance = 0;
        for (AnalyticAccount acc : analytics) {
            totalBudgetVariance += (acc.getBudgetAmount() - acc.getActualAmount());
        }

        stats.put("totalContainers", totalContainers);
        stats.put("availableYardSlots", availableSlots);
        stats.put("availableCranes", availableCranes);
        stats.put("pendingInvoices", pendingInvoices);
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalExpenses", totalExpense);
        stats.put("netProfit", netProfit);
        stats.put("budgetVariance", totalBudgetVariance);

        return stats;
    }
}
