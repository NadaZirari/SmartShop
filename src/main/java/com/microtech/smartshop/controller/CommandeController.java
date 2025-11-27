package com.microtech.smartshop.controller;


import com.microtech.smartshop.dto.CommandeDTO;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;
import com.microtech.smartshop.service.CommandeService;
import com.microtech.smartshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class CommandeController {

    private final CommandeService commandeService;
    private final AuthUtil authUtil;

    public CommandeController(CommandeService commandeService, AuthUtil authUtil) {

        this.commandeService = commandeService;
        this.authUtil = authUtil;
    }

    @PostMapping
    public ResponseEntity<CommandeDTO> create(@RequestBody CommandeDTO dto , HttpSession session) {
        authUtil.requireClientOrAdmin(session);
        User user = authUtil.getUserFromSession(session);

        // Les clients ne peuvent créer que pour eux-mêmes
        if (user.getRole() == UserRole.CLIENT && !user.getId().equals(dto.getClientId())) {
            throw new RuntimeException("Accès refusé : vous ne pouvez créer une commande que pour votre compte.");
        }
        return ResponseEntity.ok(commandeService.createCommande(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> getById(@PathVariable Long id, HttpSession session) {
        authUtil.requireClientOrAdmin(session);

        User user = authUtil.getUserFromSession(session);

        CommandeDTO commande = commandeService.getCommandeById(id, session);

        // Les clients ne peuvent voir que leurs commandes
        if (user.getRole() == UserRole.CLIENT && !commande.getClientId().equals(user.getId())) {
            throw new RuntimeException("Accès refusé : vous ne pouvez voir que vos propres commandes.");
        }

        return ResponseEntity.ok(commande);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CommandeDTO>> getByClient(@PathVariable Long clientId, HttpSession session) {
        authUtil.requireClientOrAdmin(session);
        User user = authUtil.getUserFromSession(session);

        // Les clients ne peuvent voir que leurs commandes
        if (user.getRole() == UserRole.CLIENT && !user.getId().equals(clientId)) {
            throw new RuntimeException("Accès refusé : vous ne pouvez voir que vos propres commandes.");
        }
        return ResponseEntity.ok(commandeService.getCommandesByClient(clientId));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable Long id, HttpSession session) {
        authUtil.requireAdmin(session);
        commandeService.confirmOrder(id);
        return ResponseEntity.ok("Commande confirmée avec succès !");
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id, HttpSession session) {
        authUtil.requireAdmin(session);
        commandeService.cancelOrder(id);
        return ResponseEntity.ok("Commande annulée avec succès !");
    }
}
