package com.microtech.smartshop.service;


import com.microtech.smartshop.dto.ClientDTO;
import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.CustomerTier;
import com.microtech.smartshop.enums.UserRole;
import com.microtech.smartshop.exception.BusinessException;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.mapper.ClientMapper;
import com.microtech.smartshop.repository.ClientRepository;
import com.microtech.smartshop.repository.CommandeRepository;
import com.microtech.smartshop.repository.UserRepository;

import com.microtech.smartshop.serviceImpl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientServiceImplTest {

    private ClientRepository clientRepository;
    private CommandeRepository commandeRepository;
    private ClientMapper clientMapper;
    private UserRepository userRepository;

    private ClientServiceImpl service;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        commandeRepository = mock(CommandeRepository.class);
        clientMapper = mock(ClientMapper.class);
        userRepository = mock(UserRepository.class);

        service = new ClientServiceImpl(
                clientRepository,
                commandeRepository,
                clientMapper,
                null,      // CommandeMapper non utilisé ici
                userRepository
        );
    }

    // --------------------------------------------------------------------
    // TEST CREATE CLIENT
    // --------------------------------------------------------------------
    @Test
    void createClient_success() {

        // DTO reçu
        ClientDTO dto = new ClientDTO();
        dto.setUsername("amine");
        dto.setPassword("1234");
        dto.setNom("Amine");
        dto.setEmail("amine@mail.com");

        // User saved
        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setUsername("amine");
        savedUser.setRole(UserRole.CLIENT);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Mapper : dto -> entity
        Client entity = new Client();
        when(clientMapper.toEntity(dto)).thenReturn(entity);

        // Repository : entity saved
        Client savedClient = new Client();
        savedClient.setId(100L);
        savedClient.setUser(savedUser);
        savedClient.setNiveau(CustomerTier.BASIC);

        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        // Mapper : entity -> dto
        ClientDTO returnedDto = new ClientDTO();
        returnedDto.setId(100L);
        when(clientMapper.toDto(savedClient)).thenReturn(returnedDto);

        // -------------------------
        // CALL SERVICE
        // -------------------------
        ClientDTO result = service.create(dto);

        // Assertions
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(10L, result.getUserId());
        assertEquals("amine", result.getUsername());

        // Capture client saved to DB
        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());

        Client saved = captor.getValue();
        assertEquals(CustomerTier.BASIC, saved.getNiveau());
        assertEquals(0, saved.getTotalCommandes());
        assertEquals(BigDecimal.ZERO, saved.getTotalDepense());
        assertNotNull(saved.getCreatedAt());
    }

    // --------------------------------------------------------------------
    // TEST GET BY ID
    // --------------------------------------------------------------------
    @Test
    void getById_success() {
        Client c = new Client();
        c.setId(1L);
        c.setNom("Youssef");

        User u = new User();
        u.setId(5L);
        u.setUsername("youss");
        c.setUser(u);

        when(clientRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(c));

        ClientDTO dto = new ClientDTO();
        dto.setId(1L);
        when(clientMapper.toDto(c)).thenReturn(dto);

        ClientDTO result = service.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5L, result.getUserId());
        assertEquals("youss", result.getUsername());
    }

    // --------------------------------------------------------------------
    // TEST GET BY ID - NOT FOUND
    // --------------------------------------------------------------------
    @Test
    void getById_notFound() {
        when(clientRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getById(1L);
        });
    }

    // --------------------------------------------------------------------
    // TEST DELETE CLIENT
    // --------------------------------------------------------------------
    @Test
    void delete_success() {
        Client c = new Client();
        c.setId(1L);
        c.setDeleted(false);

        when(clientRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(c));

        service.delete(1L);

        assertTrue(c.isDeleted());
        verify(clientRepository).save(c);
    }

    @Test
    void delete_alreadyDeleted() {
        Client c = new Client();
        c.setId(1L);
        c.setDeleted(true);

        when(clientRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(c));

        assertThrows(BusinessException.class, () -> {
            service.delete(1L);
        });
    }

    // --------------------------------------------------------------------
    // TEST GETLOYALTYLEVEL
    // --------------------------------------------------------------------
    @Test
    void getLoyaltyLevel_success() {
        Client c = new Client();
        c.setId(1L);
        c.setNiveau(CustomerTier.GOLD);

        when(clientRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(c));

        String level = service.getLoyaltyLevel(1L);

        assertEquals("GOLD", level);
    }

    // --------------------------------------------------------------------
    // TEST RECALCULATE LOYALTY LEVEL
    // --------------------------------------------------------------------
    @Test
    void recalculateLoyaltyLevel_success() {

        Client c = new Client();
        c.setId(1L);
        c.setNiveau(CustomerTier.BASIC);

        when(clientRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(c));

        when(commandeRepository.countConfirmedByClient(1L))
                .thenReturn(5);

        when(commandeRepository.sumTotalConfirmedByClient(1L))
                .thenReturn(6000.0);

        service.recalculateLoyaltyLevel(1L);

        assertEquals(CustomerTier.GOLD, c.getNiveau());
        verify(clientRepository).save(c);
    }
}

