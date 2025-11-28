package com.microtech.smartshop.serviceImpl;



import com.microtech.smartshop.dto.PaymentDTO;
import com.microtech.smartshop.entity.Paiement;
import com.microtech.smartshop.enums.OrderStatus;
import com.microtech.smartshop.enums.PaymentType;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.enums.PaymentStatus;
import com.microtech.smartshop.repository.CommandeRepository;
import com.microtech.smartshop.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CommandeRepository commandeRepository;

    @Transactional
    public PaymentDTO enregistrerPaiement(Long commandeId, Paiement paiement) {

        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        // Règle: paiement espèces ≤ 20 000 DH
        if (paiement.getType() == PaymentType.ESPECES
                && paiement.getMontant() > 20000) {
            paiement.setStatut(PaymentStatus.REJETE);
            paiement.setDateEncaissement(LocalDateTime.now());
            return mapToDTO(paymentRepository.save(paiement));
        }

        // Paiement fractionné autorisé
        double montantRestant = commande.getMontantRestant() - paiement.getMontant();
        commande.setMontantRestant(Math.max(0, montantRestant));

        // Si paiement > montant restant → rejeter
        if (paiement.getMontant() > commande.getMontantRestant() + paiement.getMontant()) {
            paiement.setStatut(PaymentStatus.REJETE);
        } else {
            paiement.setStatut(PaymentStatus.ENCAISSE);
            paiement.setDateEncaissement(LocalDateTime.now());
        }

        paiement.setCommande(commande);
        paymentRepository.save(paiement);

        // Mettre à jour la commande selon le paiement
        if (paiement.getStatut() == PaymentStatus.REJETE) {
            commande.setStatut(OrderStatus.REJECTED);
        } else if (commande.getMontantRestant() <= 0) {
            commande.setStatut(OrderStatus.CONFIRMED);
        }

        commandeRepository.save(commande);

        return mapToDTO(paiement);
    }


    private PaymentDTO mapToDTO(Paiement paiement) {
        return PaymentDTO.builder()
                .id(paiement.getId())
                .commandeId(paiement.getCommande().getId())
                .montant(paiement.getMontant())
                .type(paiement.getType())
                .datePaiement(paiement.getDatePaiement())
                .dateEncaissement(paiement.getDateEncaissement())
                .statut(paiement.getStatut())
                .build();
    }
}
