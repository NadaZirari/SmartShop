package com.microtech.smartshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "commande_id")
	 private Commande commande;


	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "product_id")
	 private Product produit;


	 private Integer quantite;
	 private Double prixUnitaireHT;
	 private Double totalLigne; // quantite * prixUnitaireHT
	 }
