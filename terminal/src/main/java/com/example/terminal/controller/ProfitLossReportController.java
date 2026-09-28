package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.JournalEntry;
import com.example.terminal.model.ProfitLossReport;
import com.example.terminal.repository.JournalEntryRepository;
import com.example.terminal.repository.ProfitLossReportRepository;

@RestController
@RequestMapping("/api/port/reports")
public class ProfitLossReportController {

    private final JournalEntryRepository journalRepository;
    private final ProfitLossReportRepository reportRepository;

    public ProfitLossReportController(JournalEntryRepository journalRepository,
                                     ProfitLossReportRepository reportRepository) {
        this.journalRepository = journalRepository;
        this.reportRepository = reportRepository;
    }

    @GetMapping("/profit-loss")
    public ProfitLossReport getProfitLossReport() {

        List<JournalEntry> entries = journalRepository.findAll();

        double handlingRevenue = 0;
        double demurrageRevenue = 0;
        double fuelExpense = 0;
        double maintenanceExpense = 0;

        for (JournalEntry entry : entries) {
            String acc = entry.getAccountName();
            if (acc == null) continue;

            if (acc.equalsIgnoreCase("Terminal Handling Revenue")) {
                handlingRevenue += entry.getCredit();
            } else if (acc.equalsIgnoreCase("Demurrage Revenue")) {
                demurrageRevenue += entry.getCredit();
            } else if (acc.equalsIgnoreCase("Fuel Expense") || acc.equalsIgnoreCase("Vendor Expense")) {
                fuelExpense += entry.getDebit();
            } else if (acc.equalsIgnoreCase("Maintenance Expense")) {
                maintenanceExpense += entry.getDebit();
            }
        }

        double totalRevenue = handlingRevenue + demurrageRevenue;
        double totalExpense = fuelExpense + maintenanceExpense;
        double netProfit = totalRevenue - totalExpense;

        ProfitLossReport report = new ProfitLossReport();
        report.setTotalRevenue(totalRevenue);
        report.setTotalExpense(totalExpense);
        report.setNetProfit(netProfit);

        return reportRepository.save(report);
    }
}