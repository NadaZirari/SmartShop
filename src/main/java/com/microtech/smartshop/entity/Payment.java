package com.microtech.smartshop.entity;


import com.microtech.smartshop.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Commande commande;

    private int numeroPaiement;
    private double montant;
    private String typePaiement;
    private LocalDate datePaiement;
    private LocalDate dateEncaissement;
    private PaymentStatus status;
}
