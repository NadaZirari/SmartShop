package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.CommandeDTO;

import java.util.List;

public interface CommandeService {
    CommandeDTO createCommande(CommandeDTO dto);
    CommandeDTO getCommandeById(Long id);
    List<CommandeDTO> getCommandesByClient(Long clientId);


    void confirmOrder(Long id);

    void cancelOrder(Long id);
}
