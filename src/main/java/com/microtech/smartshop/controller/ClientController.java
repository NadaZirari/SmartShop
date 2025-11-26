package com.microtech.smartshop.controller;

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
import com.microtech.smartshop.service.ClientStats;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor

public class ClientController {

    private final  ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO clientDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.create(clientDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ClientDTO>> getAllClients(Pageable pageable) {
        return ResponseEntity.ok(clientService.getAll( pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @RequestBody ClientDTO clientDTO) {
        return ResponseEntity.ok(clientService.update(id, clientDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ClientStats> getClientStats(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getStats(id));
    }
}
