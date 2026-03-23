package com.airsphereconnect.services;

import com.airsphereconnect.entities.User;
import com.airsphereconnect.repositories.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        if (user.getDeletedAt() != null) {
            throw new DisabledException("Compte utilisateur supprimé");
        }
        // Log utilisateur chargé
        log.debug("User loaded for authentication: {}", user.getUsername());

        // Retourne l'entité User qui implémente UserDetails (Spring utilisera getAuthorities() de User)
        return user;
    }
}
