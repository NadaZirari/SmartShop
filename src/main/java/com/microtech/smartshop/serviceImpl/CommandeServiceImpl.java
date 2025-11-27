package com.microtech.smartshop.serviceImpl;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CommandeServiceImpl  implements CommandeService {
    private final CommandeRepository commandeRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;


    public CommandeServiceImpl(CommandeRepository commandeRepository,
                               ProductRepository productRepository,
                               ClientRepository clientRepository,
                               AuthUtil authUtil) {
        this.commandeRepository = commandeRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
    }


    @Override
    @Transactional
    public CommandeDTO createCommande(CommandeDTO dto) {

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        if (dto.getItems() == null || dto.getItems().isEmpty())
            throw new RuntimeException("Commande doit contenir au moins un article");

        Commande commande = Commande.builder()
                .client(client)
                .statut(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .codePromo(dto.getCodePromo())
                .build();

        double sousTotal = 0.0;
        double remise = 0.0;

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product produit = productRepository.findById(itemDTO.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

            double totalLigne = produit.getPrixUnitaire() * itemDTO.getQuantite();

            if (itemDTO.getQuantite() > produit.getStockDisponible()) {
                commande.setStatut(OrderStatus.REJECTED);
                throw new RuntimeException("Quantité demandée supérieure au stock disponible pour " + produit.getNom());
            }

            OrderItem orderItem = OrderItem.builder()
                    .commande(commande)
                    .produit(produit)
                    .quantite(itemDTO.getQuantite())
                    .prixUnitaireHT(produit.getPrixUnitaire())
                    .totalLigne(totalLigne)
                    .build();

            commande.getOrderItems().add(orderItem);
            sousTotal += totalLigne;
        }

        remise += calculRemiseFidelite(client, sousTotal);

        // Calcul remise code promo
        remise += calculRemisePromo(dto.getCodePromo(), sousTotal);

        double montantHT = sousTotal - remise;
        double tva = montantHT * 0.20;
        double total = montantHT + tva;

        commande.setSousTotal(sousTotal);
        commande.setRemise(remise);
        commande.setTva(tva);
        commande.setTotal(total);
        commande.setMontantRestant(total);

        commandeRepository.save(commande);

        return mapToDTO(commande);
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
    private CommandeDTO mapToDTO(Commande c) {

        List<OrderItemDTO> items = c.getOrderItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .produitId(item.getProduit().getId())
                        .quantite(item.getQuantite())
                        .prixUnitaireHT(item.getPrixUnitaireHT())
                        .totalLigne(item.getTotalLigne())
                        .build())
                .collect(Collectors.toList());

        return CommandeDTO.builder()
                .id(c.getId())
                .clientId(c.getClient().getId())
                .sousTotal(c.getSousTotal())
                .remise(c.getRemise())
                .tva(c.getTva())
                .total(c.getTotal())
                .montantRestant(c.getMontantRestant())
                .statut(c.getStatut() != null ? c.getStatut().name() : null) // conversion enum -> String
                .codePromo(c.getCodePromo())
                .items(items)
                .build();
    }

    @Override
    public CommandeDTO getCommandeById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        return mapToDTO(commande);
    }

    @Override
    public List<CommandeDTO> getCommandesByClient(Long clientId) {
        return commandeRepository.findByClientId(clientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public void confirmOrder(Long orderId) {
        Commande commande = commandeRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (!commande.getStatut().equals(OrderStatus.PENDING)) {
            throw new RuntimeException("La commande n'est pas en statut PENDING");
        }

        if (commande.getMontantRestant() > 0) {
            throw new RuntimeException("Impossible de confirmer, paiement incomplet");
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
        client.setTotalDepense(client.getTotalDepense().add(BigDecimal.valueOf(commande.getTotal())));

        // Recalcul du niveau de fidélité
        if (client.getTotalDepense().compareTo(BigDecimal.valueOf(50000)) >= 0) {
            client.setNiveau(CustomerTier.PLATINUM);
        } else if (client.getTotalDepense().compareTo(BigDecimal.valueOf(20000)) >= 0) {
            client.setNiveau(CustomerTier.GOLD);
        } else if (client.getTotalDepense().compareTo(BigDecimal.valueOf(5000)) >= 0) {
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
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (commande.getStatut() == OrderStatus.CONFIRMED) {
            throw new RuntimeException("Impossible d’annuler une commande déjà confirmée");
        }

        commande.setStatut(OrderStatus.CANCELED);
        commandeRepository.save(commande);
    }

}
