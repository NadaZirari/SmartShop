package com.microtech.smartshop.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;


//Représente la forme standard de TOUTES les réponses d'erreur

@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp; // date et heure de l’erreur
    private int status;              // code HTTP
    private String error;            // type d’erreur (ex: "ForbiddenException")
    private String message;          // message explicatif
    private String path;             // endpoint appelé

}