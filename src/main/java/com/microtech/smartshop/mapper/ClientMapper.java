package com.microtech.smartshop.mapper;

import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.dto.ClientDTO;
import com.microtech.smartshop.enums.CustomerTier;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    @Mapping(target = "niveau", source = "niveau", qualifiedByName = "customerTierToString")
    ClientDTO toDto(Client client);

    @Mapping(target = "niveau", source = "niveau", qualifiedByName = "stringToCustomerTier")
    @Mapping(target = "totalCommandes", source = "totalCommandes", defaultValue = "0")
    @Mapping(target = "totalDepense", source = "totalDepense")
    Client toEntity(ClientDTO dto);

    @Named("customerTierToString")
    default String mapCustomerTierToString(CustomerTier niveau) {
        return niveau != null ? niveau.name() : null;
    }

    @Named("stringToCustomerTier")
    default CustomerTier mapStringToCustomerTier(String niveau) {
        return niveau != null ? CustomerTier.valueOf(niveau) : CustomerTier.BASIC;
    }

    // BigDecimal <-> Double conversions
    default Double mapTotalDepense(java.math.BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }

    default java.math.BigDecimal mapTotalDepense(Double value) {
        return value != null ? java.math.BigDecimal.valueOf(value) : java.math.BigDecimal.ZERO;
    }
}
