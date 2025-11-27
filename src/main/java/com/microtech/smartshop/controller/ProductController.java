package com.microtech.smartshop.controller;
import com.microtech.smartshop.dto.ProductDTO;
import com.microtech.smartshop.dto.ProductCreateDTO;
import com.microtech.smartshop.service.ProductService;
import com.microtech.smartshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AuthUtil authUtil;


    @PostMapping
    public ResponseEntity<ProductDTO> create(
            @Valid @RequestBody ProductCreateDTO dto,
            HttpSession session) {
        authUtil.requireAdmin(session);
        ProductDTO created = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id ,  HttpSession session) {

        authUtil.requireClientOrAdmin(session);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductCreateDTO dto , HttpSession session) {
        authUtil.requireAdmin(session);
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id , HttpSession session) {
        authUtil.requireAdmin(session);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Pagination + Sort + Filtrage
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable,
            HttpSession session) {
        authUtil.requireClientOrAdmin(session);
        Page<ProductDTO> products = productService.getAllProductsFiltered( name,  minPrice,  maxPrice, pageable);
        return ResponseEntity.ok(products);
    }



}
