package entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import enums.CustomerTier;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Client {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;
    private String telephone;

    @Enumerated(EnumType.STRING)
    private CustomerTier niveau  = CustomerTier.BASIC;

    private Integer totalCommandes = 0;
    private BigDecimal totalDepense = BigDecimal.ZERO;

    private LocalDate firstOrderDate;
    private LocalDate lastOrderDate;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Commande> commandes;

    private LocalDateTime createdAt;
    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}