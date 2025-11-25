package com.microtech.smartshop.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClientDTO {
    private Long id;
    private String nom; 
    private String email;
    private String telephone;  
    private String niveau;  
    private Integer totalCommandes;
    private Double totalDepense;
    private LocalDate firstOrderDate;  
    private LocalDate lastOrderDate;  
}
