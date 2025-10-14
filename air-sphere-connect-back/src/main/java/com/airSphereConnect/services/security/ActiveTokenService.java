package com.airSphereConnect.services.security;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveTokenService {

    private final ConcurrentHashMap<String, String> activeRefreshTokens = new ConcurrentHashMap<>();

    public void saveRefreshToken(String username, String refreshToken) {
        activeRefreshTokens.put(username, refreshToken);
    }

    public boolean isTokenActive(String username, String token) {
        return token.equals(activeRefreshTokens.get(username));
    }

}
