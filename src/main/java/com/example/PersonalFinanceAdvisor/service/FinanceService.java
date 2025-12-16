package com.example.PersonalFinanceAdvisor.service;

import com.example.PersonalFinanceAdvisor.model.Expense;
import com.example.PersonalFinanceAdvisor.model.Income;
import com.example.PersonalFinanceAdvisor.repository.ExpenseRepository;
import com.example.PersonalFinanceAdvisor.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class FinanceService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    // ---------- BASIC CRUD ----------

    public Income addIncome(Income income) {
        // make sure date is set
        if (income.getDate() == null) {
            income.setDate(LocalDate.now());
        }
        return incomeRepository.save(income);
    }

    public Expense addExpense(Expense expense) {
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }
        return expenseRepository.save(expense);
    }

    // ---------- ALL-TIME TOTALS ----------

    public Map<String, Double> getCategoryTotals() {
        List<Expense> expenses = expenseRepository.findAll();
        Map<String, Double> totals = new HashMap<>();

        for (Expense e : expenses) {
            String key = e.getCategory();
            totals.put(key, totals.getOrDefault(key, 0.0) + e.getAmount());
        }
        return totals;
    }

    public double getTotalIncome() {
        return incomeRepository.findAll()
                .stream()
                .mapToDouble(Income::getAmount)
                .sum();
    }

    // ---------- TODAY-SPECIFIC METHODS ----------

    public double getTodayIncome() {
        LocalDate today = LocalDate.now();
        return incomeRepository.findAll()
                .stream()
                .filter(i -> today.equals(i.getDate()))
                .mapToDouble(Income::getAmount)
                .sum();
    }

    public double getTodayExpense() {
        LocalDate today = LocalDate.now();
        return expenseRepository.findAll()
                .stream()
                .filter(e -> today.equals(e.getDate()))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public Map<String, Double> getTodayCategoryTotals() {
        LocalDate today = LocalDate.now();
        Map<String, Double> totals = new HashMap<>();

        expenseRepository.findAll()
                .stream()
                .filter(e -> today.equals(e.getDate()))
                .forEach(e -> {
                    String key = e.getCategory();
                    totals.put(key, totals.getOrDefault(key, 0.0) + e.getAmount());
                });

        return totals;
    }

    /**
     * Combined summary for "today":
     *  - incomeToday
     *  - expenseToday
     *  - categoryTotalsToday
     */
    public Map<String, Object> getTodaySummary() {
        double incomeToday = getTodayIncome();
        Map<String, Double> categoryTotalsToday = getTodayCategoryTotals();

        double expenseToday = categoryTotalsToday.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("incomeToday", incomeToday);
        summary.put("expenseToday", expenseToday);
        summary.put("categoryTotalsToday", categoryTotalsToday);

        return summary;
    }

    // ---------- SUGGESTIONS (ALL TIME) ----------

    public List<String> getSuggestions() {
        double income = getTotalIncome();
        Map<String, Double> categoryTotals = getCategoryTotals();

        List<String> suggestions = new ArrayList<>();

        // Make sure category names match how you save them in DB
        double food = categoryTotals.getOrDefault("Food", 0.0);
        double shopping = categoryTotals.getOrDefault("Shopping", 0.0);
        double bills = categoryTotals.getOrDefault("Bills", 0.0);

        double totalExpenses = categoryTotals.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double savings = income - totalExpenses;

        if (food > income * 0.3)
            suggestions.add("Your food budget is high; consider reducing it by around 15%.");

        if (shopping > income * 0.2)
            suggestions.add("Shopping expense is high; try reducing discretionary purchases.");

        if (savings < income * 0.1)
            suggestions.add("Low savings this month. Try reducing non-essential spending.");

        if (bills > income * 0.25)
            suggestions.add("Your bills are taking a large portion of your income. Review fixed costs.");

        if (suggestions.isEmpty())
            suggestions.add("Great job! Your spending looks well balanced.");

        return suggestions;
    }
}
