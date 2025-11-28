package com.microtech.smartshop.controller;

import com.microtech.smartshop.dto.LoginRequestDTO;
import com.microtech.smartshop.dto.UserDTO;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.exception.BusinessException;
import com.microtech.smartshop.exception.UnauthorizedActionException;
import com.microtech.smartshop.mapper.UserMapper;
import com.microtech.smartshop.serviceImpl.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @Valid @RequestBody LoginRequestDTO loginDTO, 
            HttpSession session) {
        
        try {
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            
            // Stockage session
            session.setAttribute("USER_SESSION", user.getId());
            
            // Conversion en DTO pour l'API (password exclu)
            UserDTO userDTO = userMapper.toDTO(user);
            return ResponseEntity.ok(userDTO);
            
        } catch (BusinessException ex) {
            // Erreur métier (compte désactivé)
            throw ex;
        } catch (Exception ex) {
            // Erreur d'authentification
            throw new UnauthorizedActionException("Nom d'utilisateur ou mot de passe incorrect");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
            session.invalidate(); // Supprime la session
        return ResponseEntity.ok().build();
    }
}
