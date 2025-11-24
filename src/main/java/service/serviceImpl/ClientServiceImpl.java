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
import lombok.RequiredArgsConstructor;
import repository.ClientRepository;
import service.ClientService;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

	    private final ClientRepository clientRepository;

	    @Override
	    public Client createClient(Client client) {
	        client.setNiveau(CustomerTier.BASIC);
	        client.setTotalOrders(0);
	        client.setTotalSpent(BigDecimal.ZERO);
	        return clientRepository.save(client);
	    }

	    @Override
	    public Client updateClient(Long id, Client client) {
	        Client existing = clientRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Client not found"));
	        existing.setNom(client.getNom());
	        existing.setEmail(client.getEmail());
	        return clientRepository.save(existing);
	    }

	    @Override
	    public void deleteClient(Long id) {
	        clientRepository.deleteById(id);
	    }

	    @Override
	    public Client getClient(Long id) {
	        return clientRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Client not found"));
	    }

	    @Override
	    public List<Client> getAllClients() {
	        return clientRepository.findAll();
	    }

	    @Override
	    public Map<String, Object> getClientStats(Long id) {
	        Client client = getClient(id);
	        Map<String, Object> stats = new HashMap<>();
	        stats.put("totalOrders", client.getTotalOrders());
	        stats.put("totalSpent", client.getTotalSpent());
	        stats.put("firstOrderDate", client.getCommandes().stream()
	                .map(Commande::getDate)
	                .min(LocalDateTime::compareTo)
	                .orElse(null));
	        stats.put("lastOrderDate", client.getCommandes().stream()
	                .map(Commande::getDate)
	                .max(LocalDateTime::compareTo)
	                .orElse(null));
	        stats.put("historiqueCommandes", client.getCommandes());
	        return stats;
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
