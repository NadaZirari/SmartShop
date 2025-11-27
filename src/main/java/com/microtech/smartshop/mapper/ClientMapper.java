package com.microtech.smartshop.mapper;

import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.dto.ClientDTO;
import com.microtech.smartshop.enums.CustomerTier;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    @Mapping(target = "userId", source = "user.id")          // ID de l'utilisateur lié
    @Mapping(target = "username", source = "user.username") // username
    @Mapping(target = "password", source = "user.password") // mdp
    @Mapping(target = "role", source = "user.role")         // rôle
    @Mapping(target = "niveau", source = "niveau", qualifiedByName = "customerTierToString")
    ClientDTO toDto(Client client);


    @Mapping(target = "niveau", source = "niveau", qualifiedByName = "stringToCustomerTier")
    @Mapping(target = "user", ignore = true) //  ignore le mapping User pour la création/MAJ via DTO
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
    default Double mapTotalDepense(BigDecimal value) {

        return value != null ? value.doubleValue() : 0.0;
    }

    default BigDecimal mapTotalDepense(Double value) {
        return value != null ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
    }
}
