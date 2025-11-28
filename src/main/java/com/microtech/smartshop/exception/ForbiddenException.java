package com.microtech.smartshop.exception;


// 403 - L'utilisateur est authentifié mais n’a pas les droits pour accéder à cette ressource
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);

    }
}
