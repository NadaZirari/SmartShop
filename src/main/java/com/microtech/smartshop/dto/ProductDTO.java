package com.microtech.smartshop.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductDTO {
    private Long id;
    private String nom;
    private Double prixUnitaire;
    private Integer stockDisponible;
}

