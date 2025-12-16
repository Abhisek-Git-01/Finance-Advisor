package com.example.PersonalFinanceAdvisor.repository;

import com.example.PersonalFinanceAdvisor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<User,Integer> {

    User findByMobNo(String mobNo);
    User findByEmail(String email);

    Optional<User> findByMobNoAndEmail(String mobNo, String email);
}
