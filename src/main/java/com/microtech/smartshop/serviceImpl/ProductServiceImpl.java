package com.microtech.smartshop.serviceImpl;
import com.microtech.smartshop.dto.ProductDTO;
import com.microtech.smartshop.dto.ProductCreateDTO;
import com.microtech.smartshop.entity.Product;
import com.microtech.smartshop.mapper.ProductMapper;
import com.microtech.smartshop.repository.ProductRepository;
import com.microtech.smartshop.service.ProductService;
import org.springframework.data.domain.Page;
import com.microtech.smartshop.exception.NotFoundException;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {


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
    @Override
    public ProductDTO updateProduct(Long id, ProductCreateDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec id " + id));
        productMapper.updateEntityFromDTO(dto, product);
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec id " + id));
        if(product.isDeleted()) {
            throw new NotFoundException("Produit supprimé");
        }
        return productMapper.toDTO(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec id " + id));
        product.setDeleted(true); // soft delete
        productRepository.save(product);
    }

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findByDeletedFalse(pageable)
                .map(productMapper::toDTO);
    }

}
