package mapper;

import entity.Client;
import dto.ClientDTO;
import enums.CustomerTier;
import org.mapstruct.*;
import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "niveau", source = "niveau")
    @Mapping(target = "firstOrderDate", source = "firstOrderDate")
    @Mapping(target = "lastOrderDate", source = "lastOrderDate")
    @Mapping(target = "totalCommandes", source = "totalCommandes")
    @Mapping(target = "totalDepense", source = "totalDepense")
    @Mapping(target = "telephone", source = "telephone")
    ClientDTO toDto(Client client);

    @Mapping(target = "niveau", source = "niveau")
    @Mapping(target = "firstOrderDate", source = "firstOrderDate")
    @Mapping(target = "lastOrderDate", source = "lastOrderDate")
    @Mapping(target = "totalCommandes", source = "totalCommandes", defaultValue = "0")
    @Mapping(target = "totalDepense", source = "totalDepense")
    @Mapping(target = "telephone", source = "telephone")
    Client toEntity(ClientDTO dto);

    // Handle BigDecimal <-> Double conversion
    default Double mapTotalDepense(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }

    default BigDecimal mapTotalDepense(Double value) {
        return value != null ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
    }
    
    // Handle CustomerTier to String conversion for DTO
    default String mapCustomerTier(CustomerTier niveau) {
        return niveau != null ? niveau.name() : null;
    }
    
    // Handle String to CustomerTier conversion for Entity
    default CustomerTier mapCustomerTier(String niveau) {
        return niveau != null ? CustomerTier.valueOf(niveau) : CustomerTier.BASIC;
    }
}
