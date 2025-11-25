package com.microtech.smartshop.serviceImpl;

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
import com.microtech.smartshop.exception.NotFoundException;
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

	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private CommandeRepository commandeRepository;
	@Autowired
	private ClientMapper clientMapper;
	@Autowired
	private CommandeMapper commandeMapper;

	@Override
	public ClientDTO create(ClientDTO dto) {
		Client c = clientMapper.toEntity(dto);
		c.setNiveau(CustomerTier.BASIC);
		c.setCreatedAt(LocalDateTime.now());
		c.setTotalCommandes(0);
		c.setTotalDepense(BigDecimal.ZERO);
		Client saved = clientRepository.save(c);
		return clientMapper.toDto(saved);
	}

	@Override
	public Page<ClientDTO> getAll(Pageable pageable) {
		return clientRepository.findAll(pageable).map(clientMapper::toDto);
	}

	@Override
	public ClientDTO getById(Long id) {
		Client c = clientRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new NotFoundException("Client introuvable"));
		return clientMapper.toDto(c);
	}

	@Override
	public ClientDTO update(Long id, ClientDTO dto) {
		Client c = clientRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new NotFoundException("Client introuvable"));
		if (dto.getNom() != null)
			c.setNom(dto.getNom());
		if (dto.getEmail() != null)
			c.setEmail(dto.getEmail());
		if (dto.getTelephone() != null)
			c.setTelephone(dto.getTelephone());

		Client saved = clientRepository.save(c);
		return clientMapper.toDto(saved);
	}

	@Override
	public void delete(Long id) {
		Client c = clientRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new NotFoundException("Client introuvable"));
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
				.orElseThrow(() -> new NotFoundException("Client introuvable"));
		return c.getNiveau().name();
	}

	@Override
	public void recalculateLoyaltyLevel(Long clientId) {
		Client c = clientRepository.findByIdAndDeletedFalse(clientId)
				.orElseThrow(() -> new NotFoundException("Client introuvable"));

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
