package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.JournalEntry;
import com.example.terminal.repository.JournalEntryRepository;

@RestController
@RequestMapping("/api/port/reports")
public class ProfitLossReportController {

    private final JournalEntryRepository journalRepository;

    public ProfitLossReportController(JournalEntryRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    @GetMapping("/profit-loss")
    public String getProfitLossReport() {

        List<JournalEntry> entries = journalRepository.findAll();

        double totalRevenue = 0;
        double totalExpense = 0;

        for (JournalEntry entry : entries) {

            if (entry.getAccountName().equals("Terminal Handling Revenue")
                    || entry.getAccountName().equals("Demurrage Revenue")) {

                totalRevenue += entry.getCredit();
            }

            if (entry.getAccountName().equals("Fuel Expense")) {

                totalExpense += entry.getDebit();
            }
        }

        double netProfit = totalRevenue - totalExpense;

        return "Total Revenue: ₹" + totalRevenue
                + "\nTotal Expense: ₹" + totalExpense
                + "\nNet Profit: ₹" + netProfit;
    }
}