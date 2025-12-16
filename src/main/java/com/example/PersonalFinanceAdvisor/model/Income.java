package com.example.PersonalFinanceAdvisor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double amount;
    private String category;
    private LocalDate date;

    private String userEmail;

    public Income() {}

    public Income(double amount, String category, LocalDate date, String userEmail) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.userEmail = userEmail;
    }
}
