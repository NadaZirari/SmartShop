package com.microtech.smartshop.controller;
import com.microtech.smartshop.dto.ProductDTO;
import com.microtech.smartshop.dto.ProductCreateDTO;
import com.microtech.smartshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping
    public ProductDTO createProduct(@RequestBody ProductCreateDTO dto) {
        return productService.createProduct(dto);
    }


    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }


    @GetMapping
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }


    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @RequestBody ProductCreateDTO dto) {
        return productService.updateProduct(id, dto);
    }


    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }




}
