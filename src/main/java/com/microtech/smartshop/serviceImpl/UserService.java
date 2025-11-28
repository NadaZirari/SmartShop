package com.microtech.smartshop.serviceImpl;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.exception.BusinessException;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérification simple du mot de passe (hash à prévoir en production)
        if (!user.getPassword().equals(password)) {
            throw new BusinessException("Mot de passe incorrect");
        }

        return user;
    }


    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }


}
