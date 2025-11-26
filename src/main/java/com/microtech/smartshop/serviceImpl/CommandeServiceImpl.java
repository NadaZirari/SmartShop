package com.microtech.smartshop.serviceImpl;

import com.microtech.smartshop.repository.ClientRepository;
import com.microtech.smartshop.repository.CommandeRepository;
import com.microtech.smartshop.repository.ProductRepository;
import com.microtech.smartshop.service.CommandeService;
import com.microtech.smartshop.entity.*;
import com.microtech.smartshop.enums.OrderStatus;
import com.microtech.smartshop.repository.*;
import com.microtech.smartshop.dto.*;

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

public class CommandeServiceImpl  implements CommandeService {
    private final CommandeRepository commandeRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    public CommandeServiceImpl(CommandeRepository commandeRepository,
                               ProductRepository productRepository,
                               ClientRepository clientRepository) {
        this.commandeRepository = commandeRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public CommandeDTO createCommande(CommandeCreateDTO dto) {

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

        // Calcul remise fidélité (exemple)
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
}
