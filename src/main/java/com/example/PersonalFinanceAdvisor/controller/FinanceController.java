package com.example.PersonalFinanceAdvisor.controller;

import com.example.PersonalFinanceAdvisor.model.Expense;
import com.example.PersonalFinanceAdvisor.model.Income;
import com.example.PersonalFinanceAdvisor.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService service;

    // ---------- Add income / expense ----------

    @PostMapping("/income")
    public Income addIncome(@RequestBody Income income) {
        return service.addIncome(income);
    }

    @PostMapping("/expense")
    public Expense addExpense(@RequestBody Expense expense) {
        return service.addExpense(expense);
    }

    // ---------- Overall summary (all time) ----------

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        summary.put("totalIncome", service.getTotalIncome());
        summary.put("categoryTotals", service.getCategoryTotals());
        summary.put("suggestions", service.getSuggestions()); // optional: include suggestions here

        return summary;
    }

    // ---------- Suggestions only (if you still want separate) ----------

    @GetMapping("/suggestions")
    public List<String> getSuggestions() {
        return service.getSuggestions();
    }

    // ---------- TODAY APIs (for “how much I income today and where I expenses”) ----------

    // Combined today summary: income, expense, category-wise
    @GetMapping("/today")
    public Map<String, Object> getTodaySummary() {
        return service.getTodaySummary();
    }

    // Only today’s income
    @GetMapping("/today/income")
    public double getTodayIncome() {
        return service.getTodayIncome();
    }

    // Only today’s expense
    @GetMapping("/today/expense")
    public double getTodayExpense() {
        return service.getTodayExpense();
    }
}
