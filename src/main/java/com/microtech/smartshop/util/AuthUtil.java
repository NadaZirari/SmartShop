package com.microtech.smartshop.util;

import com.microtech.smartshop.serviceImpl.UserService;
import jakarta.servlet.http.HttpSession;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AuthUtil {



    private final UserService userService;

    // Récupère l'utilisateur complet via l'id stocké en session
    public User getUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_SESSION");
        if (userId == null) return null;
        return userService.findById(userId); // UserService doit avoir findById()
    }


    public  void requireLogin(HttpSession session) {
        if (getUser(session) == null) {
            throw new RuntimeException("Vous devez être connecté.");
        }
    }

    public  void requireAdmin(HttpSession session) {
        User user = getUser(session);
        if (user == null || user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Accès refusé : réservé à l'ADMIN.");
        }
    }

    public  void requireClientOrAdmin(HttpSession session) {
        User u = getUser(session);
        if (u == null) throw new RuntimeException("Non connecté.");

        if (u.getRole() != UserRole.CLIENT && u.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Accès non autorisé.");
        }
    }
}
