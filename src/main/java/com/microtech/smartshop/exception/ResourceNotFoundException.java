package com.microtech.smartshop.exception;

// 404 - La ressource demandée n'existe pas (client, produit, commande, etc.)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}