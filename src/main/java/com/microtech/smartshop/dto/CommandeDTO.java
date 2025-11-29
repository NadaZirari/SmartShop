package com.microtech.smartshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
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

private Double sousTotal;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private Double remise;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private Double tva;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private Double total;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private Double montantRestant;
private String codePromo;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

private String statut;
    private List<OrderItemDTO> items;
}