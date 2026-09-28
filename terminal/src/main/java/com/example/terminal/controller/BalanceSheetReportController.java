package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.BalanceSheetReport;
import com.example.terminal.model.JournalEntry;
import com.example.terminal.repository.BalanceSheetReportRepository;
import com.example.terminal.repository.JournalEntryRepository;

@RestController
@RequestMapping("/api/port/reports")
public class BalanceSheetReportController {

    private final JournalEntryRepository journalRepository;
    private final BalanceSheetReportRepository reportRepository;

    public BalanceSheetReportController(JournalEntryRepository journalRepository,
                                       BalanceSheetReportRepository reportRepository) {
        this.journalRepository = journalRepository;
        this.reportRepository = reportRepository;
    }

    @GetMapping("/balance-sheet")
    public BalanceSheetReport getBalanceSheetReport() {

        List<JournalEntry> entries = journalRepository.findAll();

        double bankBalance = 0;
        double receivable = 0;
        double payable = 0;

        for (JournalEntry entry : entries) {
            String acc = entry.getAccountName();
            if (acc == null) continue;

            if (acc.equalsIgnoreCase("Bank")) {
                bankBalance += entry.getDebit();
                bankBalance -= entry.getCredit();
            } else if (acc.equalsIgnoreCase("Customer Receivable")) {
                receivable += entry.getDebit();
                receivable -= entry.getCredit();
            } else if (acc.equalsIgnoreCase("Vendor Payable")) {
                payable += entry.getCredit();
                payable -= entry.getDebit();
            }
        }

        double totalAssets = Math.max(0, bankBalance) + Math.max(0, receivable);
        double totalLiabilities = Math.max(0, payable);
        double totalEquity = totalAssets - totalLiabilities;

        BalanceSheetReport report = new BalanceSheetReport();
        report.setTotalAssets(totalAssets);
        report.setTotalLiabilities(totalLiabilities);
        report.setTotalEquity(totalEquity);

        return reportRepository.save(report);
    }
}