package com.microtech.smartshop.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long produitId;       // Id du produit
    private Integer quantite;     // Quantité commandée
    private Double prixUnitaireHT; // Prix unitaire
    private Double totalLigne;
}
