package com.example.banking_management_system;

import org.springframework.data.jpa.repository.JpaRepository;

// This interface gives CRUD operations on Accounts table
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
    java.util.List<Accounts> findByEmail(String email);
    boolean existsByCvv(String cvv);
}
