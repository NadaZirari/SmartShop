package com.microtech.smartshop.repository;





import com.microtech.smartshop.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
	List<Commande> findByClientIdOrderByDateDesc(Long clientId);


	@Query("select coalesce(sum(c.total),0) from Commande c where c.client.id = :clientId and c.statut = OrderStatus.CONFIRMED")
	Double sumTotalConfirmedByClient(@Param("clientId") Long clientId);


	@Query(value = "select count(c) from Commande c where c.client.id = :clientId and c.statut = OrderStatus.CONFIRMED")
	Integer countConfirmedByClient(@Param("clientId") Long clientId);
	

}
