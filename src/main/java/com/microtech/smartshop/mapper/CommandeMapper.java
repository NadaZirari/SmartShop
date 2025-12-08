package com.microtech.smartshop.mapper;

import com.microtech.smartshop.dto.CommandeDTO;
import com.microtech.smartshop.entity.Commande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface CommandeMapper {

    @Mapping(target = "clientId", expression = "java(commande.getClient() != null ? commande.getClient().getId() : null)")
    @Mapping(target = "items", source = "orderItems")
    CommandeDTO toDto(Commande commande);

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "orderItems", source = "items")
    Commande toEntity(CommandeDTO commandeDTO);
}