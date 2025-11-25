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

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private double prixUnitaire;

    @Column(nullable = false)
    private int stockDisponible;

    @Column(nullable = false)
    private boolean deleted = false; // Soft delete

    @OneToMany(mappedBy = "produit")
    private List<OrderItem> orderItems;
}
