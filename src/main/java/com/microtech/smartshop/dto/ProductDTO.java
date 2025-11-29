package com.microtech.smartshop.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private Long id;
    private String nom;
    private Double prixUnitaire;
    private Integer stockDisponible;
}

