package com.microtech.smartshop.exception;

// 400 - Les données envoyées ne respectent pas les règles de validation
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}