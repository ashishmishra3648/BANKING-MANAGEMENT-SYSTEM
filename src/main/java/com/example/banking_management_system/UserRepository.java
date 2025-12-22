package com.example.banking_management_system;

import org.springframework.data.jpa.repository.JpaRepository;

// This interface gives CRUD operations on User table
public interface UserRepository extends JpaRepository<User, String> {
}
