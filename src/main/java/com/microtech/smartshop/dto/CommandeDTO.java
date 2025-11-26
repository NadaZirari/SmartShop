package com.microtech.smartshop.dto;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class CommandeDTO {
private Long id;
private Long clientId;
private LocalDateTime date;
private Double sousTotal;
private Double remise;
private Double tva;
private Double total;
private Double montantRestant;
private String codePromo;
private String statut;
}