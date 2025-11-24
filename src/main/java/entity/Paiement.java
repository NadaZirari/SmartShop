package entity;

import java.time.LocalDateTime;

import enums.PaymentStatus;
import enums.PaymentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "paiements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    private Commande commande;

	    private int numeroPaiement; // 1, 2, 3 ...

	    private double montant;

	    @Enumerated(EnumType.STRING)
	    private PaymentType typePaiement; // ESPECES, CHEQUE, VIREMENT

	    private LocalDateTime datePaiement;     // Date de l’opération par le client
	    private LocalDateTime dateEncaissement; // Date effective d’encaissement

	    @Enumerated(EnumType.STRING)
	    private PaymentStatus statut = PaymentStatus.EN_ATTENTE;

}
