package com.microtech.smartshop.serviceImpl;

import com.microtech.smartshop.exception.BusinessException;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.exception.ValidationException;
import com.microtech.smartshop.mapper.CommandeMapper;
import com.microtech.smartshop.util.AuthUtil;

import com.microtech.smartshop.repository.ClientRepository;
import com.microtech.smartshop.repository.CommandeRepository;
import com.microtech.smartshop.repository.ProductRepository;
import com.microtech.smartshop.service.CommandeService;
import com.microtech.smartshop.entity.*;
import com.microtech.smartshop.enums.OrderStatus;
import com.microtech.smartshop.repository.*;
import com.microtech.smartshop.dto.*;
import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.entity.OrderItem;
import com.microtech.smartshop.enums.CustomerTier;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CommandeServiceImpl  implements CommandeService {
    private final CommandeRepository commandeRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final CommandeMapper commandeMapper;


    public CommandeServiceImpl(CommandeRepository commandeRepository,
                               ProductRepository productRepository,
                               ClientRepository clientRepository, CommandeMapper commandeMapper,
                               AuthUtil authUtil) {
        this.commandeRepository = commandeRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.commandeMapper = commandeMapper;
    }

    private static final BigDecimal TVA_RATE = new BigDecimal("0.20");

    @Override
    @Transactional
    public CommandeDTO createCommande(CommandeDTO dto) {


        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        if (dto.getItems() == null || dto.getItems().isEmpty())
            throw new ValidationException("Commande doit contenir au moins un article");

        Commande commande = Commande.builder()
                .client(client)
                .statut(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .codePromo(dto.getCodePromo())
                .date(LocalDateTime.now())
                .build();

        BigDecimal sousTotal = BigDecimal.ZERO;
        double remise = 0.0;

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product produit = productRepository.findById(itemDTO.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

            double totalLigne = produit.getPrixUnitaire() * itemDTO.getQuantite();

            if (itemDTO.getQuantite() > produit.getStockDisponible()) {
                commande.setStatut(OrderStatus.REJECTED);
                throw new BusinessException("Quantité demandée supérieure au stock disponible pour " + produit.getNom());
            }

            OrderItem orderItem = OrderItem.builder()
                    .commande(commande)
                    .produit(produit)
                    .quantite(itemDTO.getQuantite())
                    .prixUnitaireHT(produit.getPrixUnitaire())
                    .totalLigne(totalLigne)
                    .build();

            commande.getOrderItems().add(orderItem);

            // addition en BigDecimal
            sousTotal = sousTotal.add(BigDecimal.valueOf(totalLigne));
        }

// Calcul des remises
        remise += calculRemiseFidelite(client, sousTotal.doubleValue());
        remise += calculRemisePromo(dto.getCodePromo(), sousTotal.doubleValue());

// conversion remise double -> BigDecimal
        BigDecimal bdRemise = BigDecimal.valueOf(remise);

        BigDecimal montantHT = sousTotal.subtract(bdRemise).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal tva = montantHT.multiply(TVA_RATE).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal total = montantHT.add(tva).setScale(2, BigDecimal.ROUND_HALF_UP);

// Affectation
        commande.setSousTotal(sousTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
        commande.setRemise(remise); // reste double
        commande.setTva(tva);
        commande.setTotal(total);
        commande.setMontantRestant(total);


        commandeRepository.save(commande);

        return commandeMapper.toDto(commande);
    }

    private double calculRemiseFidelite(Client client, double sousTotal) {
        switch (client.getNiveau()) {
            case SILVER: return sousTotal >= 500 ? sousTotal * 0.05 : 0;
            case GOLD: return sousTotal >= 800 ? sousTotal * 0.10 : 0;
            case PLATINUM: return sousTotal >= 1200 ? sousTotal * 0.15 : 0;
            default: return 0;
        }
    }

    private double calculRemisePromo(String codePromo, double sousTotal) {
        if (codePromo != null && codePromo.matches("PROMO-[A-Z0-9]{4}")) {
            return sousTotal * 0.05;
        }
        return 0;
    }


    @Override
    public CommandeDTO getCommandeById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));
        return commandeMapper.toDto(commande);

    }

    @Override
    public List<CommandeDTO> getCommandesByClient(Long clientId) {
        return commandeRepository.findByClientId(clientId).stream()
                .map(commandeMapper::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public void confirmOrder(Long orderId) {
        Commande commande = commandeRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (!commande.getStatut().equals(OrderStatus.PENDING)) {
            throw new BusinessException("La commande n'est pas en statut PENDING");
        }

        if (commande.getMontantRestant().compareTo(BigDecimal.ZERO) > 00) {
            throw new BusinessException("Impossible de confirmer, paiement incomplet");
        }

        // Décrémentation du stock
        for (OrderItem item : commande.getOrderItems()) {
            Product produit = item.getProduit();
            produit.setStockDisponible(produit.getStockDisponible() - item.getQuantite());
            productRepository.save(produit);
        }

        // Mise à jour du statut de la commande
        commande.setStatut(OrderStatus.CONFIRMED);
        commandeRepository.save(commande);

        // Mise à jour du client
        Client client = commande.getClient();
        client.setTotalCommandes(client.getTotalCommandes() + 1);
        client.setTotalDepense(client.getTotalDepense().add(commande.getTotal()));

        // Recalcul du niveau de fidélité
        if (client.getTotalDepense().compareTo(BigDecimal.valueOf(15000)) >= 0) {
            client.setNiveau(CustomerTier.PLATINUM);
        } else if (client.getTotalDepense().compareTo(BigDecimal.valueOf(5000)) >= 0) {
            client.setNiveau(CustomerTier.GOLD);
        } else if (client.getTotalDepense().compareTo(BigDecimal.valueOf(1000)) >= 0) {
            client.setNiveau(CustomerTier.SILVER);
        } else {
            client.setNiveau(CustomerTier.BASIC);
        }

        clientRepository.save(client);
    }


    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Commande commande = commandeRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (commande.getStatut() == OrderStatus.CONFIRMED) {
            throw new BusinessException("Impossible d’annuler une commande déjà confirmée");
        }

        commande.setStatut(OrderStatus.CANCELED);
        commandeRepository.save(commande);
    }

    // GET ALL
    public List<CommandeDTO> getAll() {
        return commandeRepository.findAll()
                .stream()
                .map(commandeMapper::toDto)
                .toList();
    }



    // Supprime (soft)
    public void delete(Long id) {
        if (!commandeRepository.existsById(id))
            throw new ResourceNotFoundException("Commande introuvable");
        commandeRepository.deleteById(id);
    }
}

