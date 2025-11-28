package com.microtech.smartshop.exception;


// 422 - Une règle métier est violée (exemple : stock insuffisant, commande déjà traitée)
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}