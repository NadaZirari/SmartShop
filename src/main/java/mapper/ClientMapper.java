package mapper;

import entity.Client;
import dto.ClientDTO;
import org.mapstruct.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "tier", source = "niveau")
    @Mapping(target = "firstOrderAt", source = "firstOrderDate")
    @Mapping(target = "lastOrderAt", source = "lastOrderDate")
    @Mapping(target = "totalCommandes", source = "totalCommandes")
    @Mapping(target = "totalDepense", source = "totalDepense")
    ClientDTO toDto(Client client);

    @Mapping(target = "niveau", source = "tier")
    @Mapping(target = "firstOrderDate", source = "firstOrderAt")
    @Mapping(target = "lastOrderDate", source = "lastOrderAt")
    @Mapping(target = "totalCommandes", source = "totalCommandes", defaultValue = "0")
    @Mapping(target = "totalDepense", source = "totalDepense")
    Client toEntity(ClientDTO dto);

    // Handle int <-> Integer conversion
    default Integer mapTotalCommandes(int value) {
        return value;
    }

    default int mapTotalCommandes(Integer value) {
        return value != null ? value : 0;
    }

    // Handle BigDecimal <-> Double conversion
    default Double mapTotalDepense(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }

    default BigDecimal mapTotalDepense(Double value) {
        return value != null ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
    }

    // Handle LocalDate <-> LocalDateTime conversion
    default LocalDateTime mapLocalDateToLocalDateTime(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    default LocalDate mapLocalDateTimeToLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }
}
