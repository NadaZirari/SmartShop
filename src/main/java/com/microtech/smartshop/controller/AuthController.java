package com.microtech.smartshop.controller;

import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.serviceImpl.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        User user = userService.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.status(401).body("Identifiants invalides.");
        }

        session.setAttribute("user", user);

        return ResponseEntity.ok("Connexion réussie ! Rôle : " + user.getRole());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Déconnecté avec succès.");
    }
}
