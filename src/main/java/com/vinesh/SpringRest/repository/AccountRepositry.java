package com.vinesh.SpringRest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinesh.SpringRest.model.Account;

public interface AccountRepositry extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
}
