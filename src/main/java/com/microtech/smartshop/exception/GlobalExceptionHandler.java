package com.microtech.smartshop.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

// Centralise la gestion de TOUTES les exceptions du projet
@ControllerAdvice
public class GlobalExceptionHandler {

    // Méthode utilitaire pour construire la réponse JSON
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex, HttpStatus status, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, status);
    }


    // 400 - Erreur de validation
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex, HttpServletRequest request) {

        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }


    // 401 - Non authentifié
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedActionException ex, HttpServletRequest request) {

        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    // 403 - Accès interdit
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex, HttpServletRequest request) {

        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, request);
    }


    // 404 - Ressource inexistante
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }


    // 422 - Règle métier violée
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex, HttpServletRequest request) {

        return buildErrorResponse(ex, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    // 500 - Exception générale
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {

        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }