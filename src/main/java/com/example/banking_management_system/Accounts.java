package com.example.banking_management_system;

import jakarta.persistence.*;

@Entity
@Table(name = "accounts")
public class Accounts {
    @Id
    private Long account_number;

    private String full_name;
    private String email;
    private Double balance;
    private String security_pin;
    private String cvv;
    private String expiryDate;

    // Getters and setters
    public Long getAccount_number() { return account_number; }
    public void setAccount_number(Long account_number) { this.account_number = account_number; }
    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public String getSecurity_pin() { return security_pin; }
    public void setSecurity_pin(String security_pin) { this.security_pin = security_pin; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
