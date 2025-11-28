package com.microtech.smartshop.serviceImpl;



import com.microtech.smartshop.dto.PaymentDTO;
import com.microtech.smartshop.entity.Paiement;
import com.microtech.smartshop.enums.OrderStatus;
import com.microtech.smartshop.enums.PaymentType;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.enums.PaymentStatus;
import com.microtech.smartshop.repository.CommandeRepository;
import com.microtech.smartshop.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CommandeRepository commandeRepository;
    private final PaymentMapper paymentMapper;



    @Transactional
    public PaymentDTO enregistrerPaiement(Long commandeId, Paiement paiement) {

        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        paiement.setCommande(commande);
        paiement.setDatePaiement(LocalDateTime.now());

        // Règle: paiement espèces ≤ 20 000 DH
        if (paiement.getType() == PaymentType.ESPECES && paiement.getMontant() > 20000) {
            paiement.setStatut(PaymentStatus.REJETE);
            paiement.setDateEncaissement(LocalDateTime.now());
        } else {
            // Paiement fractionné autorisé
            double montantRestantAvant = commande.getMontantRestant();
            if (paiement.getMontant() > montantRestantAvant) {
                paiement.setStatut(PaymentStatus.REJETE);
                paiement.setDateEncaissement(LocalDateTime.now());
            } else {
                paiement.setStatut(PaymentStatus.ENCAISSE);
                paiement.setDateEncaissement(LocalDateTime.now());
                commande.setMontantRestant(Math.max(0, montantRestantAvant - paiement.getMontant()));
            }
        }

        Paiement savedPaiement = paymentRepository.save(paiement);

        // Mise à jour statut commande
        if (paiement.getStatut() == PaymentStatus.REJETE) {
            commande.setStatut(OrderStatus.REJECTED);
        } else if (commande.getMontantRestant() <= 0) {
            commande.setStatut(OrderStatus.CONFIRMED);
        }
        commandeRepository.save(commande);

        return paymentMapper.toDTO(savedPaiement);
    }

    @Transactional
    public PaymentDTO enregistrerPaiement(Long commandeId, PaymentDTO paymentDTO) {
        Paiement paiement = paymentMapper.toEntity(paymentDTO);
        return enregistrerPaiement(commandeId, paiement); // appel à l'autre méthode
    }
    @Transactional
    public PaymentDTO validatePayment(Long paiementId) {
        Paiement paiement = paymentRepository.findById(paiementId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        paiement.setStatut(PaymentStatus.ENCAISSE);
        paiement.setDateEncaissement(LocalDateTime.now());
        paymentRepository.save(paiement);
        return paymentMapper.toDTO(paiement);
    }

    @Transactional
    public PaymentDTO rejectPayment(Long paiementId) {
        Paiement paiement = paymentRepository.findById(paiementId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        paiement.setStatut(PaymentStatus.REJETE);
        paiement.setDateEncaissement(LocalDateTime.now());
        paymentRepository.save(paiement);
        return paymentMapper.toDTO(paiement);
    }

    public List<PaymentDTO> getPaymentsByOrderId(Long commandeId) {
        return paymentRepository.findByCommandeId(commandeId).stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }
}
