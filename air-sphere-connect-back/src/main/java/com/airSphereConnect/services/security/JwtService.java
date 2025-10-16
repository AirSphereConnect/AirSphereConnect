package com.airSphereConnect.services.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);
    Date extractExpiration(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    Claims extractAllClaims(String token); // Correction ici
    boolean isTokenExpired(String token);
    boolean validateToken(String token, UserDetails userDetails);
    String generateAccessToken(UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
    String generateToken(UserDetails userDetails, long validityMillis);
    String generateGuestToken();// Internal method
    List<String> extractRoles(String token);
    UserDetails extractUserDetails(String token);
    int getAccessTokenExpirySeconds();
    int getRefreshTokenExpirySeconds();
}

