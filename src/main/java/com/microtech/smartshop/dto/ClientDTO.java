package com.microtech.smartshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClientDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;      // ID de l'utilisateur associé


    @NotBlank(message = "Username est obligatoire")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Mot de passe est obligatoire")
    private String password;

    private String email;

    private String telephone;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String niveau;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer totalCommandes;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal totalDepense;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private LocalDate firstOrderDate;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private LocalDate lastOrderDate;  
}
