package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.JournalEntry;
import com.example.terminal.repository.JournalEntryRepository;

@RestController
@RequestMapping("/api/port/reports")
public class BalanceSheetReportController {

    private final JournalEntryRepository journalRepository;

    public BalanceSheetReportController(JournalEntryRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    @GetMapping("/balance-sheet")
    public String getBalanceSheetReport() {

        List<JournalEntry> entries = journalRepository.findAll();

        double bankBalance = 0;
        double receivable = 0;
        double payable = 0;

        for (JournalEntry entry : entries) {

            if (entry.getAccountName().equals("Bank")) {
                bankBalance += entry.getDebit();
                bankBalance -= entry.getCredit();
            }

            if (entry.getAccountName().equals("Customer Receivable")) {
                receivable += entry.getDebit();
                receivable -= entry.getCredit();
            }

            if (entry.getAccountName().equals("Vendor Payable")) {
                payable += entry.getCredit();
                payable -= entry.getDebit();
            }
        }

        double totalAssets = bankBalance + receivable;
        double totalLiabilities = payable;

        return "Total Assets: ₹" + totalAssets
                + "\nTotal Liabilities: ₹" + totalLiabilities;
    }
}