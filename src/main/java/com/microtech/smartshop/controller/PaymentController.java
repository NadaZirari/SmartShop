package com.microtech.smartshop.controller;


import com.microtech.smartshop.mapper.PaymentMapper;
import com.microtech.smartshop.repository.CommandeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microtech.smartshop.dto.PaymentDTO;

import com.microtech.smartshop.entity.Paiement;
import com.microtech.smartshop.serviceImpl.PaymentService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final CommandeRepository commandeRepository;

    private final PaymentMapper paymentMapper;


    @PostMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> addPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentDTO paymentDTO) {

        PaymentDTO savedPayment = paymentService.enregistrerPaiement(orderId, paymentDTO);
        return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
    }

    // Valider un paiement
    @PutMapping("/{id}/validate")
    public ResponseEntity<PaymentDTO> validatePayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.validatePayment(id));
    }

    // Rejeter un paiement
    @PutMapping("/{id}/reject")
    public ResponseEntity<PaymentDTO> rejectPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.rejectPayment(id));
    }

    // Récupérer tous les paiements d'une commande
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }
}


