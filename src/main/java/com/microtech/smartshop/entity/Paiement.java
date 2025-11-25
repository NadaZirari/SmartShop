package com.microtech.smartshop.entity;

import java.time.LocalDateTime;

import com.microtech.smartshop.enums.PaymentStatus;
import com.microtech.smartshop.enums.PaymentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "paiements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "commande_id")
	 private Commande commande;


	 private Integer numeroPaiement; // séquentiel par commande
	 private Double montant;


	 @Enumerated(EnumType.STRING)
	 private PaymentType type;

	 
	 private String reference; // chq/ref/virement
	 private String banque;
	    private LocalDateTime datePaiement;     // Date de l’opération par le client
	    private LocalDateTime dateEncaissement; // Date effective d’encaissement

	    @Enumerated(EnumType.STRING)
	    private PaymentStatus statut = PaymentStatus.EN_ATTENTE;

}
