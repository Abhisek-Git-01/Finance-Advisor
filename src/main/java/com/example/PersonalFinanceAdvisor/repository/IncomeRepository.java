package com.example.PersonalFinanceAdvisor.repository;

import com.example.PersonalFinanceAdvisor.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income,Integer> {
}
