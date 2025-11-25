package com.microtech.smartshop.mapper;

import com.microtech.smartshop.entity.Product;
import com.microtech.smartshop.dto.ProductDTO;
import com.microtech.smartshop.dto.ProductCreateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
    Product toEntity(ProductCreateDTO dto);

    // update un produit existant
    void updateEntityFromDTO(ProductCreateDTO dto, @MappingTarget Product product);
}
