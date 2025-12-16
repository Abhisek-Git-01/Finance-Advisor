package com.example.PersonalFinanceAdvisor.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private  String mobNo;
    private  String email;
    private String password;

}
