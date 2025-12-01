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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CommandeRepository commandeRepository;
    private final PaymentMapper paymentMapper;


        private static final BigDecimal LIMITE_ESPECES = new BigDecimal("20000");


        // CRÉER PAIEMENT
        @Transactional
        public PaymentDTO creerPaiement(PaymentDTO dto) {
            Commande commande = commandeRepository.findById(dto.getCommandeId())
                    .orElseThrow(() -> new RuntimeException("Commande introuvable"));

            if (commande.getMontantRestant().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Commande déjà totalement payée");
            }

            if (dto.getType() == PaymentType.ESPECES && dto.getMontant().compareTo(LIMITE_ESPECES) > 0) {
                throw new RuntimeException("Paiement espèces dépasse la limite légale de 20,000 DH");
            }

            Paiement paiement = paymentMapper.toEntity(dto);
            paiement.setCommande(commande);

            // Numéro séquentiel
            long numero = paymentRepository.countByCommande(commande) + 1;
            paiement.setNumeroPaiement(numero);

            LocalDateTime now = LocalDateTime.now();
            paiement.setDatePaiement(now);

            // Gestion de l'encaissement selon type
            if (dto.getType() == PaymentType.ESPECES || dto.getType() == PaymentType.VIREMENT) {
                paiement.setDateEncaissement(now);
                paiement.setStatut(PaymentStatus.ENCAISSE);
            } else { // CHÈQUE
                paiement.setDateEncaissement(null);
                paiement.setStatut(PaymentStatus.EN_ATTENTE);
            }

            Paiement saved = paymentRepository.save(paiement);

            // Mise à jour montant restant de la commande
            if (saved.getStatut() == PaymentStatus.ENCAISSE && saved.getMontant() != null) {
                BigDecimal nouveauRestant = commande.getMontantRestant().subtract(saved.getMontant());
                commande.setMontantRestant(nouveauRestant.max(BigDecimal.ZERO));
                commandeRepository.save(commande);
            }

            return paymentMapper.toDTO(saved);
        }

        // MODIFIER STATUS / ENCAISSER
        @Transactional
        public PaymentDTO mettreAJourStatus(Long id, PaymentStatus status, LocalDateTime dateEncaissement) {
            Paiement paiement = paymentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

            paiement.setStatut(status);

            if (status == PaymentStatus.ENCAISSE && paiement.getDateEncaissement() == null) {
                paiement.setDateEncaissement(dateEncaissement != null ? dateEncaissement : LocalDateTime.now());

                Commande cmd = paiement.getCommande();
                BigDecimal nouveauRestant = cmd.getMontantRestant().subtract(paiement.getMontant());
                cmd.setMontantRestant(nouveauRestant.max(BigDecimal.ZERO));
                commandeRepository.save(cmd);
            }

            return paymentMapper.toDTO(paymentRepository.save(paiement));
        }


        // ANNULER PAIEMENT
        @Transactional
            public PaymentDTO annulerPaiement(Long id) {
            Paiement paiement = paymentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

            Commande cmd = paiement.getCommande();
            if (paiement.getStatut() == PaymentStatus.ENCAISSE) {
                cmd.setMontantRestant(cmd.getMontantRestant().add(paiement.getMontant()));
                commandeRepository.save(cmd);
            }

            paymentRepository.delete(paiement);
            return paymentMapper.toDTO(paiement);
        }

        // GET ALL PAIEMENTS
        public List<PaymentDTO> getAll() {
            return paymentRepository.findAll().stream()
                    .map(paymentMapper::toDTO)
                    .collect(Collectors.toList());
        }

        // GET PAIEMENTS D'UNE COMMANDE
        public List<PaymentDTO> getByCommande(Long commandeId) {
            Commande commande = commandeRepository.findById(commandeId)
                    .orElseThrow(() -> new RuntimeException("Commande introuvable"));

            return paymentRepository.findByCommandeOrderByNumeroPaiementAsc(commande).stream()
                    .map(paymentMapper::toDTO)
                    .collect(Collectors.toList());
        }

        // GET ONE PAIEMENT
        public PaymentDTO getById(Long id) {
            Paiement paiement = paymentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paiement introuvable"));
            return paymentMapper.toDTO(paiement);
        }
}
