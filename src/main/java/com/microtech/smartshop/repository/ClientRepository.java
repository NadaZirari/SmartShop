package com.microtech.smartshop.repository;

import java.util.Optional;

import com.microtech.smartshop.dto.ClientDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.microtech.smartshop.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);
    Optional<Client> findByIdAndDeletedFalse(Long id);
}
