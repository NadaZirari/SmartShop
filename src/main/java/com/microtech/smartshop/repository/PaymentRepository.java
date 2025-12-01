package com.microtech.smartshop.repository;



import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Paiement, Long> {
    List<Paiement> findByCommandeId(Long commandeId);
    List<Paiement> findByCommandeOrderByNumeroPaiementAsc(Commande commande);

    Integer countByCommande(Commande commande);
}
