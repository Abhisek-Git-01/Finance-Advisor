package com.example.PersonalFinanceAdvisor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String category;
    private double amount;
    private LocalDate date;

    private String userEmail;  // <-- Add this field to link expense to a specific user

    // Constructors
    public Expense() {}

    public Expense(String category, double amount, LocalDate date, String userEmail) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.userEmail = userEmail;
    }

    // Getters + Setters
    // (if you use Lombok, you can add @Data)
}
