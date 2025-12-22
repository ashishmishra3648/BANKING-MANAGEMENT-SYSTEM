package com.example.banking_management_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*") // Allows your frontend to access the backend
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        if (userRepo.existsById(user.getEmail())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "User Already Exists!"));
        }
        userRepo.save(user);
        return ResponseEntity.ok(Collections.singletonMap("message", "User Registered!"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        User found = userRepo.findById(user.getEmail()).orElse(null);
        if (found != null && found.getPassword().equals(user.getPassword())) {
            Map<String, String> response = new java.util.HashMap<>();
            response.put("message", "Login Successful!");
            response.put("email", found.getEmail());
            response.put("full_name", found.getFull_name());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(Collections.singletonMap("message", "Invalid Credentials!"));
    }
}
