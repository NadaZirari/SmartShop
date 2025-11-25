package com.microtech.smartshop.entity;


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
    private Order order;

    private int numeroPaiement;
    private double montant;
    private String typePaiement;
    private LocalDate datePaiement;
    private LocalDate dateEncaissement;
    private PaymentStatus status;
}
