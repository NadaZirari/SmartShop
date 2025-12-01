package com.microtech.smartshop.mapper;

import com.microtech.smartshop.entity.Product;
import com.microtech.smartshop.dto.ProductDTO;
import com.microtech.smartshop.dto.ProductCreateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
    Product toEntity(ProductCreateDTO dto);

    // update un produit existant
    @Mapping(target = "id", ignore = true) // on ne met pas à jour l'id
    @Mapping(target = "deleted", ignore = true) // on ne touche pas au deleted
    void updateEntityFromDTO(ProductCreateDTO dto, @MappingTarget Product product);
}
