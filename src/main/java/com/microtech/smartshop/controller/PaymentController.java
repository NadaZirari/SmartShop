package com.microtech.smartshop.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microtech.smartshop.dto.PaymentDTO;

import com.microtech.smartshop.entity.Paiement;
import com.microtech.smartshop.serviceImpl.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{id}/payments")
    public ResponseEntity<PaymentDTO> enregistrerPaiement(
            @PathVariable Long id,
            @RequestBody Paiement paiement) {

        PaymentDTO savedPaymentDTO = paymentService.enregistrerPaiement(id, paiement);
        return ResponseEntity.ok(savedPaymentDTO);
    }


}