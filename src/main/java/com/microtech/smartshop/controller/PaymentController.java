package com.microtech.smartshop.controller;


import com.microtech.smartshop.enums.PaymentStatus;
import com.microtech.smartshop.mapper.PaymentMapper;
import com.microtech.smartshop.repository.CommandeRepository;
import com.microtech.smartshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microtech.smartshop.dto.PaymentDTO;

import com.microtech.smartshop.entity.Paiement;
import com.microtech.smartshop.serviceImpl.PaymentService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final CommandeRepository commandeRepository;

    private final PaymentMapper paymentMapper;
    private final AuthUtil authUtil;

    @PostMapping
    public ResponseEntity<PaymentDTO> creerPaiement(@RequestBody PaymentDTO dto, HttpSession session) {
        authUtil.requireAdmin(session); // seulement ADMIN
        return ResponseEntity.ok(paymentService.creerPaiement(dto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentDTO> mettreAJourStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateEncaissement
    ) {
        return ResponseEntity.ok(paymentService.mettreAJourStatus(id, status, dateEncaissement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PaymentDTO> annulerPaiement(@PathVariable Long id,HttpSession session) {
        authUtil.requireAdmin(session);
        return ResponseEntity.ok(paymentService.annulerPaiement(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAll( HttpSession session) {
        authUtil.requireAdmin(session);
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getById(@PathVariable Long id, HttpSession session) {
        authUtil.requireAdmin(session);

        return ResponseEntity.ok(paymentService.getById(id));
    }
}


