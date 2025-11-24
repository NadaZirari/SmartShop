package service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import dto.ClientDTO;
import dto.CommandeDTO;
import entity.Client;

public interface ClientService {
	ClientDTO create(ClientDTO dto);
	Page<ClientDTO> getAll(Pageable pageable);
	ClientDTO getById(Long id);
	ClientDTO update(Long id, ClientDTO dto);
	void delete(Long id);


	List<CommandeDTO> getCommandeHistory(Long clientId);
	ClientStats getStats(Long clientId);
	String getLoyaltyLevel(Long clientId);
	void recalculateLoyaltyLevel(Long clientId);

}
