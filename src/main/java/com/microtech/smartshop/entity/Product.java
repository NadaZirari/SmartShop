package com.microtech.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private double prixUnitaire;

    private int stockDisponible;

    private boolean deleted = false; // Soft delete

    @OneToMany(mappedBy = "produit")
    private List<OrderItem> orderItems;
}
