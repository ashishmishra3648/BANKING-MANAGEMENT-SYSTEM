package com.example.banking_management_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/accounts")
public class AccountsController {

    @Autowired
    private AccountsRepository accountsRepo;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody Accounts account) {
        try {
            if (!userRepo.existsById(account.getEmail())) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found. Please register first."));
            }

            // Validate Security PIN (4-6 digits)
            if (account.getSecurity_pin() == null || !account.getSecurity_pin().matches("\\d{4,6}")) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Security PIN must be 4 to 6 digits."));
            }

            // Generate Unique 10-digit Account Number
            // Range: 1,000,000,000 to 9,999,999,999
            long accountNumber;
            do {
                accountNumber = 1_000_000_000L + (long)(Math.random() * 9_000_000_000L);
            } while (accountsRepo.existsById(accountNumber));
            account.setAccount_number(accountNumber);

            // Generate Unique 3-digit CVV
            String cvv;
            int attempts = 0;
            do {
                if(attempts++ > 2000) throw new RuntimeException("Cannot generate unique CVV, limit reached.");
                int randomCvv = 100 + (int)(Math.random() * 900); // 100 to 999
                cvv = String.valueOf(randomCvv);
            } while (accountsRepo.existsByCvv(cvv));
            account.setCvv(cvv);

            // Set Expiry Date (5 years from now)
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate expiry = now.plusYears(5);
            String expiryFormatted = String.format("%02d/%02d", expiry.getMonthValue(), expiry.getYear() % 100);
            account.setExpiryDate(expiryFormatted);

            Accounts savedAccount = accountsRepo.save(account);
            return ResponseEntity.ok(savedAccount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/{account_number}")
    public ResponseEntity<?> getAccount(@PathVariable Long account_number) {
        return accountsRepo.findById(account_number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<?> getAccountsByEmail(@PathVariable String email) {
        try {
            java.util.List<Accounts> accounts = accountsRepo.findByEmail(email);
            if (accounts.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> payload) {
        Long accountNumber = Long.valueOf(payload.get("account_number").toString());
        Double amount = Double.valueOf(payload.get("amount").toString());
        if (amount <= 0) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Amount must be positive"));
        }
        Accounts acc = accountsRepo.findById(accountNumber).orElse(null);
        if (acc == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account not found"));
        }
        acc.setBalance(acc.getBalance() + amount);
        accountsRepo.save(acc);
        return ResponseEntity.ok(Collections.singletonMap("balance", acc.getBalance()));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> payload) {
        Long accountNumber = Long.valueOf(payload.get("account_number").toString());
        Double amount = Double.valueOf(payload.get("amount").toString());
        if (amount <= 0) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Amount must be positive"));
        }
        Accounts acc = accountsRepo.findById(accountNumber).orElse(null);
        if (acc == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account not found"));
        }
        if (acc.getBalance() < amount) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Insufficient funds"));
        }
        acc.setBalance(acc.getBalance() - amount);
        accountsRepo.save(acc);
        return ResponseEntity.ok(Collections.singletonMap("balance", acc.getBalance()));
    }
}
