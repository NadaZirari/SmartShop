package com.microtech.smartshop.mapper;

import com.microtech.smartshop.dto.PaymentDTO;
import com.microtech.smartshop.entity.Paiement;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDTO toDTO(Paiement paiement) {
        if (paiement == null) return null;

        return PaymentDTO.builder()
                .id(paiement.getId())
                .commandeId(paiement.getCommande() != null ? paiement.getCommande().getId() : null)
                .montant(paiement.getMontant())
                .type(paiement.getType())
                .datePaiement(paiement.getDatePaiement())
                .dateEncaissement(paiement.getDateEncaissement())
                .statut(paiement.getStatut())
                .build();
    }

    public Paiement toEntity(PaymentDTO dto) {
        if (dto == null) return null;

        return Paiement.builder()
                .id(dto.getId())
                .montant(dto.getMontant())
                .type(dto.getType())
                .datePaiement(dto.getDatePaiement())
                .dateEncaissement(dto.getDateEncaissement())
                .statut(dto.getStatut())
                .build();
    }
}
