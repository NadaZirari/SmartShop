package com.microtech.smartshop.controller;


import com.microtech.smartshop.dto.CommandeDTO;
import com.microtech.smartshop.service.CommandeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @PostMapping
    public ResponseEntity<CommandeDTO> create(@RequestBody CommandeDTO dto) {
        return ResponseEntity.ok(commandeService.createCommande(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.getCommandeById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CommandeDTO>> getByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(commandeService.getCommandesByClient(clientId));
    }
}
