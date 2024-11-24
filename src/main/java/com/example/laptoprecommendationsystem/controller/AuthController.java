// Create a controller for user registration/login validation
package com.example.laptoprecommendationsystem.controller;

import com.example.laptoprecommendationsystem.dto.UserRequestDTO;
import com.example.laptoprecommendationsystem.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ValidationService validationService;

    // POST endpoint for registration
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRequestDTO userRequest) {
        Map<String, String> response = new HashMap<>();

        // Validate email
        if (!validationService.isEmailValid(userRequest.getEmail())) {
            response.put("error", "Invalid email address.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Validate password
        if (!validationService.isPasswordValid(userRequest.getPassword())) {
            response.put("error", "Password must be at least 8 characters, containing letters and numbers.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Validate confirm password (only for registration)
        if (userRequest.getConfirmPassword() != null &&
                !validationService.doPasswordsMatch(userRequest.getPassword(), userRequest.getConfirmPassword())) {
            response.put("error", "Passwords do not match.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // If validation is successful
        response.put("message", "Validation successful!");
        return ResponseEntity.ok(response);
    }

    // POST endpoint for login (you can use the same validation as above without confirm password)
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserRequestDTO userRequest) {
        Map<String, String> response = new HashMap<>();

        // Validate email
        if (!validationService.isEmailValid(userRequest.getEmail())) {
            response.put("error", "Invalid email address.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Validate password
        if (!validationService.isPasswordValid(userRequest.getPassword())) {
            response.put("error", "Invalid password format.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // If validation is successful
        response.put("message", "Validation successful!");
        return ResponseEntity.ok(response);
    }
}
