package repository;
import entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {
	 // Liste des produits non supprimés
    Page<Product> findByDeletedFalse(Pageable pageable);

    // Recherche filtrée par nom ou prix
    Page<Product> findByDeletedFalseAndNomContainingIgnoreCase(String nom, Pageable pageable);

}
