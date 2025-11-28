package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.CommandeDTO;

import java.util.List;

public interface CommandeService {
    CommandeDTO createCommande(CommandeDTO dto);
    CommandeDTO getCommandeById(Long id);
    List<CommandeDTO> getCommandesByClient(Long clientId);


    void confirmOrder(Long id);

    void cancelOrder(Long id);

    // Récupérer toutes les commandes (pour admin)
    List<CommandeDTO> getAll();

    // Supprimer une commande (soft delete ou delete réelle)
    void delete(Long  id);
}
