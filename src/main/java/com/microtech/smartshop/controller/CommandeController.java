package com.microtech.smartshop.controller;


import com.microtech.smartshop.dto.CommandeDTO;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;
import com.microtech.smartshop.exception.UnauthorizedActionException;
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

        // CLIENT ne peut créer que pour lui-même
        if (user.getRole() == UserRole.CLIENT && !user.getClient().getId().equals(dto.getClientId())) {
            throw new UnauthorizedActionException("Accès refusé : vous ne pouvez créer une commande que pour votre propre compte.");
        }

        CommandeDTO created = commandeService.createCommande(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<CommandeDTO> getById(@PathVariable Long id, HttpSession session) {
        authUtil.requireClientOrAdmin(session);

        User user = authUtil.getUserFromSession(session);

        CommandeDTO commande = commandeService.getCommandeById(id);

        // CLIENT ne peut consulter que ses commandes
        if (user.getRole() == UserRole.CLIENT && !user.getClient().getId().equals(commande.getClientId())) {
            throw new UnauthorizedActionException("Accès refusé : vous ne pouvez voir que vos propres commandes.");
        }

        return ResponseEntity.ok(commande);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CommandeDTO>> getByClient(@PathVariable Long clientId, HttpSession session) {
        authUtil.requireClientOrAdmin(session);
        User user = authUtil.getUserFromSession(session);

        // CLIENT ne peut consulter que son historique
        if (user.getRole() == UserRole.CLIENT && !user.getClient().getId().equals(clientId)) {
            throw new UnauthorizedActionException("Accès refusé : vous ne pouvez voir que vos propres commandes.");
        }

        List<CommandeDTO> commandes = commandeService.getCommandesByClient(clientId);
        return ResponseEntity.ok(commandes);

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

    // GET ALL
    @GetMapping
    public ResponseEntity<List<CommandeDTO>> getAll( HttpSession session) {
        authUtil.requireAdmin(session);
        List<CommandeDTO> list = commandeService.getAll();
        return ResponseEntity.ok(list);
    }



    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpSession session) {
        authUtil.requireAdmin(session);
        commandeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
