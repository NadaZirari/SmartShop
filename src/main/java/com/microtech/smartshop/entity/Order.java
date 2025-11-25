package com.microtech.smartshop.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.Builder;
import enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    private LocalDateTime dateCreated;
    private double subTotalHT;
    private double remise; // remise totale appliquée
    private double tva; // 20% par défaut
    private double totalTTC;
    private String codePromo; // optional
    private OrderStatus status;
    private double montantRestant; // reste à payer
}
