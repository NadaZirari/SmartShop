package com.microtech.smartshop.serviceImpl;
import com.microtech.smartshop.dto.ProductDTO;
import com.microtech.smartshop.dto.ProductCreateDTO;
import com.microtech.smartshop.entity.Product;
import com.microtech.smartshop.mapper.ProductMapper;
import com.microtech.smartshop.repository.ProductRepository;
import com.microtech.smartshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl {


    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDTO createProduct(ProductCreateDTO dto) {
        Product product = productMapper.toEntity(dto);
        product.setDeleted(false);
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

}
