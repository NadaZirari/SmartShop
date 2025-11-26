package com.microtech.smartshop.util;

import jakarta.servlet.http.HttpSession;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;


public class AuthUtil {



    public static User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    public static void requireLogin(HttpSession session) {
        if (getUser(session) == null) {
            throw new RuntimeException("Vous devez être connecté.");
        }
    }

    public static void requireAdmin(HttpSession session) {
        User user = getUser(session);
        if (user == null || user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Accès refusé : réservé à l'ADMIN.");
        }
    }

    public static void requireClientOrAdmin(HttpSession session) {
        User u = getUser(session);
        if (u == null) throw new RuntimeException("Non connecté.");

        if (u.getRole() != UserRole.CLIENT && u.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Accès non autorisé.");
        }
    }
}
