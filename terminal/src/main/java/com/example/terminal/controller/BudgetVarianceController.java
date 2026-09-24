package com.example.terminal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.terminal.model.AnalyticAccount;
import com.example.terminal.model.BudgetVariance;
import com.example.terminal.repository.AnalyticAccountRepository;
import com.example.terminal.repository.BudgetVarianceRepository;

@RestController
@RequestMapping("/api/port/budget-variance")
public class BudgetVarianceController {

    private final BudgetVarianceRepository varianceRepository;
    private final AnalyticAccountRepository analyticRepository;

    public BudgetVarianceController(
            BudgetVarianceRepository varianceRepository,
            AnalyticAccountRepository analyticRepository) {

        this.varianceRepository = varianceRepository;
        this.analyticRepository = analyticRepository;
    }

    @GetMapping
    public List<BudgetVariance> getAllVariances() {
        return varianceRepository.findAll();
    }

    @PostMapping("/{analyticAccountId}")
    public BudgetVariance createVariance(
            @PathVariable Long analyticAccountId) {

        AnalyticAccount account = analyticRepository
                .findById(analyticAccountId)
                .orElseThrow();

        BudgetVariance variance = new BudgetVariance();

        variance.setAccountName(account.getAccountName());
        variance.setBudgetAmount(account.getBudgetAmount());
        variance.setActualAmount(account.getActualAmount());

        double varianceAmount =
                account.getBudgetAmount() - account.getActualAmount();

        variance.setVarianceAmount(varianceAmount);

        return varianceRepository.save(variance);
    }
}