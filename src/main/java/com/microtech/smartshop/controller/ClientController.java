package com.microtech.smartshop.controller;

import com.microtech.smartshop.enums.UserRole;
import com.microtech.smartshop.exception.UnauthorizedActionException;
import com.microtech.smartshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.microtech.smartshop.dto.ClientDTO;
import lombok.RequiredArgsConstructor;
import com.microtech.smartshop.service.ClientService;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor

public class ClientController {

    private final  ClientService clientService;
    private final AuthUtil authUtil;

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO , HttpSession session ) {
        authUtil.requireAdmin(session);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.create(clientDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable Long id , HttpSession session) {
        authUtil.requireClientOrAdmin(session);

        var user = authUtil.getUserFromSession(session);

        // Si CLIENT → il ne peut voir que son propre profil
        if (user.getRole() == UserRole.CLIENT) {
            Long connectedClientId = user.getClient().getId();
            if (!connectedClientId.equals(id)) {
                throw new UnauthorizedActionException("Accès refusé : vous ne pouvez consulter que votre propre profil.");
            }
        }
        return ResponseEntity.ok(clientService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ClientDTO>> getAllClients(Pageable pageable, HttpSession session) {
        authUtil.requireAdmin(session);
        return ResponseEntity.ok(clientService.getAll( pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @Valid @RequestBody ClientDTO clientDTO , HttpSession session) {
        authUtil.requireAdmin(session);
        return ResponseEntity.ok(clientService.update(id, clientDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id ,HttpSession session) {
        authUtil.requireAdmin(session);
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ClientDTO> getClientStats(@PathVariable Long id , HttpSession session) {
        authUtil.requireClientOrAdmin(session);

        var user = authUtil.getUserFromSession(session);

        // Si CLIENT → ne peut consulter que ses propres stats
        if (user.getRole() == UserRole.CLIENT) {
            Long connectedClientId = user.getClient().getId();
            if (!connectedClientId.equals(id)) {
                throw new UnauthorizedActionException("Accès refusé : vous ne pouvez voir que vos propres statistiques.");
            }
        }
        return ResponseEntity.ok(clientService.getStats(id));
    }
}
