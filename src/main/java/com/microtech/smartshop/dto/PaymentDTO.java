package com.microtech.smartshop.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microtech.smartshop.enums.PaymentType;
import com.microtech.smartshop.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class PaymentDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private Long id;
    private Long commandeId;
    private BigDecimal montant;
    private PaymentType type;
    private LocalDateTime datePaiement;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    @Schema(hidden = true)
    private LocalDateTime dateEncaissement;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private PaymentStatus statut;
}
