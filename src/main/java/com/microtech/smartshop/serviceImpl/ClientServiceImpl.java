package com.microtech.smartshop.serviceImpl;

import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;
import com.microtech.smartshop.exception.BusinessException;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.repository.UserRepository;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.microtech.smartshop.dto.ClientDTO;
import com.microtech.smartshop.dto.CommandeDTO;
import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.enums.CustomerTier;
import jakarta.transaction.Transactional;
import com.microtech.smartshop.mapper.ClientMapper;
import com.microtech.smartshop.mapper.CommandeMapper;
import com.microtech.smartshop.repository.ClientRepository;
import com.microtech.smartshop.repository.CommandeRepository;
import com.microtech.smartshop.service.ClientService;
import com.microtech.smartshop.service.ClientStats;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {


	private final ClientRepository clientRepository;

	private final CommandeRepository commandeRepository;
	private final ClientMapper clientMapper;
	private final CommandeMapper commandeMapper;
    private final UserRepository userRepository;

    @Override
    public ClientDTO create(ClientDTO dto) {
        // 1. Créer User
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // hash recommandé !
        user.setRole(UserRole.CLIENT);
        User savedUser = userRepository.save(user);

        // 2. Créer Client
        Client c = clientMapper.toEntity(dto);
        c.setUser(savedUser); // association
        c.setNiveau(CustomerTier.BASIC);
        c.setCreatedAt(LocalDateTime.now());
        c.setTotalCommandes(0);
        c.setTotalDepense(BigDecimal.ZERO);

        Client savedClient = clientRepository.save(c);

        ClientDTO result = clientMapper.toDto(savedClient);
        result.setUserId(savedUser.getId());
        result.setUsername(savedUser.getUsername());

        return result;
    }

	@Override
	public Page<ClientDTO> getAll(Pageable pageable) {
		return clientRepository.findAll(pageable).map(clientMapper::toDto);
	}

	@Override
	public ClientDTO getById(Long id) {
		Client c = clientRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));
        ClientDTO dto = clientMapper.toDto(c);
        dto.setUserId(c.getUser().getId());
        dto.setUsername(c.getUser().getUsername());
        return dto;
	}

    @Override
    public ClientDTO update(Long id, ClientDTO dto) {
        Client client = clientRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        if (dto.getNom() != null)
            client.setNom(dto.getNom());
        if (dto.getEmail() != null)
            client.setEmail(dto.getEmail());
        if (dto.getTelephone() != null)
            client.setTelephone(dto.getTelephone());

        // Optionnel : mise à jour du username dans User
        if (dto.getUsername() != null) {
            User user = client.getUser();
            user.setUsername(dto.getUsername());
            if (dto.getPassword() != null) {
                user.setPassword(dto.getPassword()); // hash recommandé
            }
            userRepository.save(user);
        }

        Client savedClient = clientRepository.save(client);
        ClientDTO result = clientMapper.toDto(savedClient);
        result.setUserId(savedClient.getUser().getId());
        result.setUsername(savedClient.getUser().getUsername());
        return result;
    }

	@Override
	public void delete(Long id) {
		Client c = clientRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

        if (c.isDeleted()) {
            throw new BusinessException("Ce client est déjà supprimé");
        }
		c.setDeleted(true);
		clientRepository.save(c);
	}

	@Override
	public List<CommandeDTO> getCommandeHistory(Long clientId) {
		List<Commande> commandes = commandeRepository.findByClientIdOrderByDateDesc(clientId);
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
	public String getLoyaltyLevel(Long clientId) {
		Client c = clientRepository.findByIdAndDeletedFalse(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));
		return c.getNiveau().name();
	}

	@Override
	public void recalculateLoyaltyLevel(Long clientId) {
		Client c = clientRepository.findByIdAndDeletedFalse(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Client introuvable"));

		Integer confirmedCount = commandeRepository.countConfirmedByClient(clientId);
		Double total = commandeRepository.sumTotalConfirmedByClient(clientId);
		if (confirmedCount == null)
			confirmedCount = 0;
		if (total == null)
			total = 0.0;

		// Règles métiers
		CustomerTier newNiveau = CustomerTier.BASIC;
		if (confirmedCount >= 20 || total >= 15000)
			newNiveau = CustomerTier.PLATINUM;
		else if (confirmedCount >= 10 || total >= 5000)
			newNiveau = CustomerTier.GOLD;
		else if (confirmedCount >= 3 || total >= 1000)
			newNiveau = CustomerTier.SILVER;

		c.setTotalCommandes(confirmedCount);
		c.setTotalDepense(BigDecimal.valueOf(round2(total)));
		c.setNiveau(newNiveau);

		List<Commande> commandes = commandeRepository.findByClientIdOrderByDateDesc(clientId);
		if (!commandes.isEmpty()) {
			c.setLastOrderDate(commandes.get(0).getDate().toLocalDate());
			c.setFirstOrderDate(commandes.get(commandes.size() - 1).getDate().toLocalDate());
		}

		clientRepository.save(c);
	}

	private Double round2(Double v) {
		return Math.round(v * 100.0) / 100.0;

	}

}
