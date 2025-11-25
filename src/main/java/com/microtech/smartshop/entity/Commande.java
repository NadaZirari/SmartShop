package com.microtech.smartshop.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.microtech.smartshop.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Client ayant passé la commande */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    private LocalDateTime date= LocalDateTime.now();

    private double sousTotal;
    private double remise;
    private double tva;
    private double total;
    private double montantRestant;

    private String codePromo;

    @Enumerated(EnumType.STRING)
    private OrderStatus statut = OrderStatus.PENDING;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paiement> paiements;

	

	
}
