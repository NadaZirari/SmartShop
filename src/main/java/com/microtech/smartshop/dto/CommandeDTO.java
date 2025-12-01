package com.microtech.smartshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Builder
@Data
public class CommandeDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

private Long clientId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private LocalDateTime date;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private BigDecimal sousTotal;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private Double remise;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private BigDecimal tva;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private BigDecimal total;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private BigDecimal montantRestant;
private String codePromo;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private String statut;
    private List<OrderItemDTO> items;
}