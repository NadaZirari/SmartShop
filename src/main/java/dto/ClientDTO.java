package dto;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class ClientDTO {
	private Long id;
	private String name;
	private String email;
	private String phone;
	private String tier;
	private Integer totalCommandes;
	private Double totalDepense;
	private LocalDateTime firstOrderAt;
	private LocalDateTime lastOrderAt;

}
