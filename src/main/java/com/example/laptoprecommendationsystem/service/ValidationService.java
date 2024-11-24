// Create a new service for validation
package com.example.laptoprecommendationsystem.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ValidationService {

    // Email regex pattern
    private static final String EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

    // Password regex pattern (minimum 8 characters, at least one letter and one number)
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_REGEX);

    public boolean isEmailValid(String email) {
        return email != null && emailPattern.matcher(email).matches();
    }

    public boolean isPasswordValid(String password) {
        return password != null && passwordPattern.matcher(password).matches();
    }

    public boolean doPasswordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }
}
