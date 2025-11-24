package entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Client {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;

    @Enumerated(EnumType.STRING)
    private CustomerTier niveau = CustomerTier.BASIC;

    private int totalOrders = 0;
    private BigDecimal totalSpent = BigDecimal.ZERO;

    private LocalDate firstOrderDate;
    private LocalDate lastOrderDate;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Commande> commandes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public CustomerTier getNiveau() {
		return niveau;
	}

	public void setNiveau(CustomerTier niveau) {
		this.niveau = niveau;
	}

	public int getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(int totalOrders) {
		this.totalOrders = totalOrders;
	}

	public BigDecimal getTotalSpent() {
		return totalSpent;
	}

	public void setTotalSpent(BigDecimal zero) {
		this.totalSpent = zero;
	}

	public LocalDate getFirstOrderDate() {
		return firstOrderDate;
	}

	public void setFirstOrderDate(LocalDate firstOrderDate) {
		this.firstOrderDate = firstOrderDate;
	}

	public LocalDate getLastOrderDate() {
		return lastOrderDate;
	}

	public void setLastOrderDate(LocalDate lastOrderDate) {
		this.lastOrderDate = lastOrderDate;
	}

	public List<Commande> getCommandes() {
		return commandes;
	}

	public void setCommandes(List<Commande> commandes) {
		this.commandes = commandes;
	}
}