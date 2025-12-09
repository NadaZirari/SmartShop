package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.ProductDTO;
import com.microtech.smartshop.dto.ProductCreateDTO;
import com.microtech.smartshop.entity.Product;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.mapper.ProductMapper;
import com.microtech.smartshop.repository.ProductRepository;
import com.microtech.smartshop.serviceImpl.ProductServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    // ----------------- createProduct -----------------
    @Test
    void createProduct_success() {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setNom("Produit1");
        dto.setPrixUnitaire(100.0);

        Product productEntity = new Product();
        when(productMapper.toEntity(dto)).thenReturn(productEntity);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setNom("Produit1");
        savedProduct.setDeleted(false);
        when(productRepository.save(productEntity)).thenReturn(savedProduct);

        ProductDTO returnedDTO = new ProductDTO();
        returnedDTO.setId(1L);
        returnedDTO.setNom("Produit1");
        when(productMapper.toDTO(savedProduct)).thenReturn(returnedDTO);

        ProductDTO result = productService.createProduct(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Produit1", result.getNom());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertFalse(captor.getValue().isDeleted());
    }

    // ----------------- updateProduct -----------------
    @Test
    void updateProduct_success() {
        Long id = 1L;
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setNom("Updated");

        Product existing = new Product();
        existing.setId(id);
        when(productRepository.findById(id)).thenReturn(Optional.of(existing));

        doNothing().when(productMapper).updateEntityFromDTO(dto, existing);

        Product saved = new Product();
        saved.setId(id);
        saved.setNom("Updated");
        when(productRepository.save(existing)).thenReturn(saved);

        ProductDTO returnedDTO = new ProductDTO();
        returnedDTO.setId(id);
        returnedDTO.setNom("Updated");
        when(productMapper.toDTO(saved)).thenReturn(returnedDTO);

        ProductDTO result = productService.updateProduct(id, dto);

        assertNotNull(result);
        assertEquals("Updated", result.getNom());

        verify(productMapper).updateEntityFromDTO(dto, existing);
        verify(productRepository).save(existing);
    }

    @Test
    void updateProduct_notFound() {
        Long id = 1L;
        ProductCreateDTO dto = new ProductCreateDTO();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(id, dto));
    }

    // ----------------- getProductById -----------------
    @Test
    void getProductById_success() {
        Long id = 1L;
        Product p = new Product();
        p.setId(id);
        p.setDeleted(false);
        when(productRepository.findById(id)).thenReturn(Optional.of(p));

        ProductDTO dto = new ProductDTO();
        dto.setId(id);
        when(productMapper.toDTO(p)).thenReturn(dto);

        ProductDTO result = productService.getProductById(id);
        assertEquals(id, result.getId());
    }

    @Test
    void getProductById_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(1L));
    }

    @Test
    void getProductById_deleted() {
        Product p = new Product();
        p.setDeleted(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(1L));
    }

    // ----------------- deleteProduct -----------------
    @Test
    void deleteProduct_success() {
        Product p = new Product();
        p.setId(1L);
        p.setDeleted(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        productService.deleteProduct(1L);

        assertTrue(p.isDeleted());
        verify(productRepository).save(p);
    }

    @Test
    void deleteProduct_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> productService.deleteProduct(1L));
    }

    // ----------------- getAllProducts -----------------
    @Test
    void getAllProducts_success() {
        Pageable pageable = Pageable.unpaged();
        Product p = new Product();
        ProductDTO dto = new ProductDTO();
        dto.setId(1L);

        when(productRepository.findByDeletedFalse(pageable))
                .thenReturn(new PageImpl<>(List.of(p)));
        when(productMapper.toDTO(p)).thenReturn(dto);

        Page<ProductDTO> result = productService.getAllProducts(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }

    // ----------------- getAllProductsFiltered -----------------
    @Test
    void getAllProductsFiltered_success() {
        Pageable pageable = Pageable.unpaged();
        Product p = new Product();
        ProductDTO dto = new ProductDTO();
        dto.setId(1L);

        when(productRepository.findAllByDeletedFalseAndNomContainingIgnoreCaseAndPrixUnitaireBetween(
                anyString(), anyDouble(), anyDouble(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(p)));
        when(productMapper.toDTO(p)).thenReturn(dto);

        Page<ProductDTO> result = productService.getAllProductsFiltered("test", 50.0, 200.0, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }
}