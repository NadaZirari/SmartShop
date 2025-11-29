package com.microtech.smartshop.controller;

import com.microtech.smartshop.dto.LoginRequestDTO;
import com.microtech.smartshop.dto.UserDTO;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.mapper.UserMapper;
import com.microtech.smartshop.serviceImpl.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginDTO, HttpSession session) {
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

        // Stockage session
        session.setAttribute("USER_SESSION", user.getId());

        // Conversion en DTO pour l'API (password exclu)
        UserDTO userDTO = userMapper.toDTO(user);
        return ResponseEntity.ok(userDTO);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // Supprime la session
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));

    }
}
