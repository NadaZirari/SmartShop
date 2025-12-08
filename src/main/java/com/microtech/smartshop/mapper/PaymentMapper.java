package com.microtech.smartshop.mapper;

import com.microtech.smartshop.dto.PaymentDTO;
import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.entity.Paiement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "commande.id", target = "commandeId")
    PaymentDTO toDTO(Paiement paiement);

    @Mapping(source = "commandeId", target = "commande.id")
    Paiement toEntity(PaymentDTO dto);
}
