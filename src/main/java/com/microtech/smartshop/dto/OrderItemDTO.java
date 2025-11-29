package com.microtech.smartshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
@Data
@Builder

public class OrderItemDTO {
    private Long produitId;       // Id du produit
    private Integer quantite;     // Quantité commandée

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private Double prixUnitaireHT; // Prix unitaire
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private Double totalLigne;
}
