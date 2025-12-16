package com.example.PersonalFinanceAdvisor.repository;

import com.example.PersonalFinanceAdvisor.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense,Integer> {
}
