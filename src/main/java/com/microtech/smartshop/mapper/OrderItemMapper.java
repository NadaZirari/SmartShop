package com.microtech.smartshop.mapper;


import com.microtech.smartshop.dto.OrderItemDTO;
import com.microtech.smartshop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "produitId", source = "produit.id")
    @Mapping(target = "prixUnitaireHT", source = "prixUnitaireHT")
    OrderItemDTO toDto(OrderItem orderItem);

    @Mapping(target = "produit", ignore = true) // Géré séparément
    @Mapping(target = "commande", ignore = true)
    OrderItem toEntity(OrderItemDTO orderItemDTO);
}
