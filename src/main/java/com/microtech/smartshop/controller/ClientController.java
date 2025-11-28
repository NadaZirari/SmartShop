package com.microtech.smartshop.controller;

import com.microtech.smartshop.dto.ClientDTO;
import com.microtech.smartshop.enums.UserRole;
import com.microtech.smartshop.exception.ForbiddenException;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.service.ClientService;
import com.microtech.smartshop.service.ClientStats;
import com.microtech.smartshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor

public class ClientController {

    private final  ClientService clientService;
    private final AuthUtil authUtil;

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(
            @Valid @RequestBody ClientDTO clientDTO,
            HttpSession session) {
        
        authUtil.requireAdmin(session);
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(clientService.create(clientDTO));
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la création du client", ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClient(
            @PathVariable Long id,
            HttpSession session) {
            
        authUtil.requireClientOrAdmin(session);
        var user = authUtil.getUserFromSession(session);

        // Si CLIENT → il ne peut voir que son propre profil
        if (user.getRole() == UserRole.CLIENT) {
            Long connectedClientId = user.getClient().getId();
            if (!connectedClientId.equals(id)) {
                throw new RuntimeException("Accès refusé : vous ne pouvez consulter que votre propre profil.");
            }
        }

        try {
            ClientDTO client = clientService.getById(id);
            if (client == null) {
                throw new ResourceNotFoundException("Client non trouvé avec l'ID : " + id);
            }
            return ResponseEntity.ok(client);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la récupération du client", ex);
        }
    }

    @GetMapping
    public ResponseEntity<Page<ClientDTO>> getAllClients(
            Pageable pageable,
            HttpSession session) {
            
        authUtil.requireAdmin(session);
        try {
            return ResponseEntity.ok(clientService.getAll(pageable));
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la récupération des clients", ex);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDTO clientDTO,
            HttpSession session) {
            
        authUtil.requireAdmin(session);
        try {
            return ResponseEntity.ok(clientService.update(id, clientDTO));
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la mise à jour du client", ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable Long id,
            HttpSession session) {
            
        authUtil.requireAdmin(session);
        try {
            // Vérifier d'abord si le client existe
            ClientDTO existingClient = clientService.getById(id);
            if (existingClient == null) {
                throw new ResourceNotFoundException("Client non trouvé avec l'ID : " + id);
            }
            
            clientService.delete(id);
            return ResponseEntity.noContent().build();
            
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la suppression du client", ex);
        }
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ClientStats> getClientStats(
            @PathVariable Long id,
            HttpSession session) {
            
        authUtil.requireClientOrAdmin(session);
        var user = authUtil.getUserFromSession(session);

        // Si CLIENT → ne peut consulter que ses propres stats
        if (user.getRole() == UserRole.CLIENT) {
            Long connectedClientId = user.getClient().getId();
            if (!connectedClientId.equals(id)) {
                throw new RuntimeException("Accès refusé : vous ne pouvez voir que vos propres statistiques.");
            }
        }

        try {
            ClientStats stats = clientService.getStats(id);
            if (stats == null) {
                throw new ResourceNotFoundException("Aucune statistique trouvée pour le client avec l'ID : " + id);
            }
            return ResponseEntity.ok(stats);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la récupération des statistiques du client", ex);
        }
    }
}
