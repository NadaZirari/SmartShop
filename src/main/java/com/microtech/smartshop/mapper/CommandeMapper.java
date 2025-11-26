package com.microtech.smartshop.mapper;

import com.microtech.smartshop.dto.CommandeDTO;
import com.microtech.smartshop.entity.Commande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface CommandeMapper {



    @Mapping(target = "clientId", expression = "java(commande.getClient() != null ? commande.getClient().getId() : null)")
    CommandeDTO toDto(Commande commande);

    @Mapping(target = "client", ignore = true)
    Commande toEntity(CommandeDTO commandeDTO);


}
