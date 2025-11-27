package com.microtech.smartshop.util;

import com.microtech.smartshop.serviceImpl.UserService;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserService userService;

    public static final String SESSION_USER_KEY = "USER_SESSION";

    // Récupère l'utilisateur complet depuis la session
    public User getUserFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_KEY);
        if (userId == null) throw new RuntimeException("Utilisateur non connecté");
        return userService.findById(userId);
    }

    public void requireLogin(HttpSession session) {
        if (getUserFromSession(session) == null) {
            throw new RuntimeException("Vous devez être connecté.");
        }
    }

    public void requireAdmin(HttpSession session) {
        User user = getUserFromSession(session);
        if (user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Accès refusé : réservé à l'ADMIN.");
        }
    }

    public void requireClientOrAdmin(HttpSession session) {
        User user = getUserFromSession(session);
        if (user.getRole() != UserRole.CLIENT && user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Accès non autorisé.");
        }
    }
}
