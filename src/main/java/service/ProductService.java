package service;
import entity.Product;
import org.springframework.data.domain.Page;

import java.util.Optional;
public interface ProductService {
	
	Product createProduct(Product product);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id); // soft delete

    Optional<Product> getProduct(Long id);

    Page<Product> getAllProducts(int page, int size, String search);

}
