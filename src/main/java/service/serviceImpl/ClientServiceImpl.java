package service.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import entity.Client;
import entity.Commande;
import enums.CustomerTier;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import repository.ClientRepository;
import service.ClientService;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

	private final ClientRepository clientRepository;
	private final CommandeRepository commandeRepository;
	private final ClientMapper clientMapper;
	private final CommandeMapper commandeMapper;


	@Override
	public ClientDTO create(ClientDTO dto) {
	Client c = clientMapper.toEntity(dto);
	c.setNiveau(CustomerTier.BASIC);
	c.setCreatedAt(LocalDateTime.now());
	c.setTotalCommandes(0);
	c.setTotalDepense(0.0);
	Client saved = clientRepository.save(c);
	return clientMapper.toDto(saved);
	}
	
	
	@Override
	public Page<ClientDTO> getAll(Pageable pageable) {
	return clientRepository.findAll(pageable).map(clientMapper::toDto);
	}


	@Override
	public ClientDTO getById(Long id) {
	Client c = clientRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Client introuvable"));
	return clientMapper.toDto(c);
	}
	
	
	

	@Override
	public ClientDTO update(Long id, ClientDTO dto) {
	Client c = clientRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Client introuvable"));
	if (dto.getNom() != null) c.setNom(dto.getNom());
	if (dto.getEmail() != null) c.setEmail(dto.getEmail());
	if (dto.getTelephone() != null) c.setTelephone(dto.getTelephone());
	Client saved = clientRepository.save(c);
	return clientMapper.toDto(saved);
	}


	@Override
	public void delete(Long id) {
	Client c = clientRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Client introuvable"));
	c.setDeleted(true);
	clientRepository.save(c);
	}
	

	  
	@Override
	public List<CommandeDTO> getCommandeHistory(Long clientId) {
	List<com.smartshop.commande.entite.Commande> commandes = commandeRepository.findByClientIdOrderByDateDesc(clientId);
	return commandes.stream().map(commandeMapper::toDto).collect(Collectors.toList());
	}


	@Override
	public ClientStats getStats(Long clientId) {
	Integer count = commandeRepository.countConfirmedByClient(clientId);
	Double sum = commandeRepository.sumTotalConfirmedByClient(clientId);
	ClientStats s = new ClientStats();
	s.setTotalCommandes(count == null ? 0 : count);
	s.setTotalDepense(sum == null ? 0.0 : round2(sum));
	return s;
	}

	    @Override
	    public void updateFidelityLevel(Client client) {
	        int totalOrders = client.getTotalOrders();
	        BigDecimal totalSpent = client.getTotalSpent(); // BigDecimal pour l'argent

	        if (totalOrders >= 20 || totalSpent.compareTo(BigDecimal.valueOf(15000)) >= 0) {
	            client.setNiveau(CustomerTier.PLATINUM);
	        } else if (totalOrders >= 10 || totalSpent.compareTo(BigDecimal.valueOf(5000)) >= 0) {
	            client.setNiveau(CustomerTier.GOLD);
	        } else if (totalOrders >= 3 || totalSpent.compareTo(BigDecimal.valueOf(1000)) >= 0) {
	            client.setNiveau(CustomerTier.SILVER);
	        } else {
	            client.setNiveau(CustomerTier.BASIC);
	        }
	    
	    clientRepository.save(client);
	    }

}
