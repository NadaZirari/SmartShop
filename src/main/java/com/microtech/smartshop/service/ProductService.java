package com.microtech.smartshop.service;
import com.microtech.smartshop.entity.Product;
import org.springframework.data.domain.Page;
import com.microtech.smartshop.dto.ProductDTO;
import com.microtech.smartshop.dto.ProductCreateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
public interface ProductService {
    ProductDTO createProduct(ProductCreateDTO dto);
    ProductDTO updateProduct(Long id, ProductCreateDTO dto);
    ProductDTO getProductById(Long id);
    void deleteProduct(Long id); // soft delete
    Page<ProductDTO> getAllProducts(Pageable pageable);
}
