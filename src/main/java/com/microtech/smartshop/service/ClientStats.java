package com.microtech.smartshop.service;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientStats {
    private Integer totalCommandes;
    private BigDecimal totalDepense;
}