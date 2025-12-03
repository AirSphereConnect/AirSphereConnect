package com.airSphereConnect.services;

import com.airSphereConnect.entities.User;
import com.airSphereConnect.repositories.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

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
        System.out.println("User loaded for authentication: " + user.getUsername());

        // Optionnel : assure toi que les rôles ont le préfixe "ROLE_" si tu gères plus d'un rôle dans une liste
        // Ici si tu as une seule propriété role Enum, ce n'est pas nécessaire
        String roleName = user.getRole() != null ? user.getRole().name() : "ROLE_GUEST";
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        List<GrantedAuthority> authorities = List.of(authority);

        // Si tu souhaites modifier les autorités dans user (plusieurs rôles), fais attention ici

        // Retourne l'entité User qui implémente UserDetails (Spring utilisera getAuthorities() de User)
        return user;
    }
}
