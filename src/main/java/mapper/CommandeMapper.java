package mapper;

import dto.CommandeDTO;
import entity.Commande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommandeMapper {
    CommandeMapper INSTANCE = Mappers.getMapper(CommandeMapper.class);
    
    @Mapping(target = "clientId", source = "client.id")
    CommandeDTO toDto(Commande commande);
    
    @Mapping(target = "client", ignore = true)
    Commande toEntity(CommandeDTO commandeDTO);
}
