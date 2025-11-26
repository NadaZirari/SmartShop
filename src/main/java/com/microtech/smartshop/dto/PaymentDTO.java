package com.microtech.smartshop.dto;


import java.time.LocalDateTime;
import com.microtech.smartshop.enums.PaymentType;
import com.microtech.smartshop.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class PaymentDTO {
    private Long id;
    private Long commandeId;
    private Double montant;
    private PaymentType type;
    private LocalDateTime datePaiement;
    private LocalDateTime dateEncaissement;
    private PaymentStatus statut;
}
