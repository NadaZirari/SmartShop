package com.microtech.smartshop.exception;

// 401 - L'utilisateur tente d'accéder à une ressource sans être authentifié
public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}