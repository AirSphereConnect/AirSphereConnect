package com.airSphereConnect.services.security;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Service gérant les tokens de rafraîchissement actifs.
 * Ce service conserve en mémoire les tokens de rafraîchissement
 * actuellement valides associés aux noms d'utilisateurs.
 */
@Service
public class ActiveTokenService {

    private final ConcurrentHashMap<String, String> activeRefreshTokens = new ConcurrentHashMap<>();

    /**
     * Enregistre ou met à jour un token de rafraîchissement actif pour un utilisateur donné.
     *
     * @param username     nom d'utilisateur unique
     * @param refreshToken token de rafraîchissement à associer
     */
    public void saveRefreshToken(String username, String refreshToken) {
        activeRefreshTokens.put(username, refreshToken);
    }

    /**
     * Vérifie si un token de rafraîchissement donné est actif pour un utilisateur spécifique.
     *
     * @param username nom d'utilisateur
     * @param token    token de rafraîchissement à valider
     * @return true si le token est actif et correspond à celui stocké, sinon false
     */
    public boolean isTokenActive(String username, String token) {
        return token.equals(activeRefreshTokens.get(username));
    }
}
