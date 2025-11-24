package mapper;

import entity.Client;
import dto.ClientDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDTO toDto(Client c);
    Client toEntity(ClientDTO dto);
}
