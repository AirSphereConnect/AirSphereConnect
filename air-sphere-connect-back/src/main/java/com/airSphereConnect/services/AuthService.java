package com.airSphereConnect.services;

import com.airSphereConnect.dtos.request.LoginRequestDto;
import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.services.security.ActiveTokenService;
import com.airSphereConnect.services.security.CookieService;
import com.airSphereConnect.services.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service d'authentification et gestion des utilisateurs.
 * Offre inscription, connexion, édition, déconnexion, suppression et mise à jour du profil utilisateur.
 */
@Service
public class AuthService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final ActiveTokenService activeTokenService;
    private final JwtService jwtService;
    private final CookieService cookieService;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String GUEST_ROLE = "GUEST";

    public AuthService(
            UserService userService,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            ActiveTokenService activeTokenService,
            UserMapper userMapper,
            CookieService cookieService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.activeTokenService = activeTokenService;
        this.userMapper = userMapper;
        this.cookieService = cookieService;
    }

    /**
     * Crée un cookie d'accès avec le token donné.
     */
    private void writeAccessTokenCookie(HttpServletResponse response, String token) {
        response.addCookie(cookieService.createCookie(ACCESS_TOKEN, token));
    }

    /**
     * Extrait le token JWT depuis les cookies de la requête.
     * @return Optional du token JWT s'il est présent, sinon Optional vide.
     */
    private Optional<String> extractJwtFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();
        for (Cookie cookie : request.getCookies()) {
            if (ACCESS_TOKEN.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return Optional.of(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    /**
     * Retourne la réponse pour un utilisateur invité avec un token invité.
     */
    @NotNull
    private ResponseEntity<?> getGuestResponse(HttpServletResponse response) {
        String guestToken = jwtService.generateGuestToken();
        writeAccessTokenCookie(response, guestToken);

        Map<String, Object> body = new HashMap<>();
        body.put("role", GUEST_ROLE);
        body.put("user", null);
        return ResponseEntity.ok(body);
    }

    /**
     * Inscrit un nouvel utilisateur puis le connecte en automatique.
     * @param dto données utilisateur d'inscription
     * @param response réponse HTTP pour ajouter les cookies
     * @return réponse HTTP avec token et données utilisateur
     */
    public ResponseEntity<?> signupAndLogin(UserRequestDto dto, HttpServletResponse response) {
        User created = userService.createUser(UserMapper.toEntity(dto));
        LoginRequestDto loginDto = new LoginRequestDto(created.getUsername(), dto.getPassword());
        return login(loginDto, response);
    }

    /**
     * Modifie login/mot de passe utilisateur ; déconnecte si modifié.
     * @param reqDto données modifiées
     * @param currentUser utilisateur connecté courant
     * @param request requête HTTP
     * @param response réponse HTTP
     * @return réponse HTTP 200 ou connexion forcée avec cookies mis à jour
     */
    public ResponseEntity<?> editUserLogin(UserRequestDto reqDto, User currentUser,
                                           HttpServletRequest request, HttpServletResponse response) {
        User userToUpdate = UserMapper.toEntity(reqDto);
        User updatedUser = userService.updateUser(currentUser.getId(), userToUpdate);

        boolean usernameChanged = reqDto.getUsername() != null && !reqDto.getUsername().isEmpty();
        boolean passwordChanged = reqDto.getPassword() != null && !reqDto.getPassword().isEmpty();

        if (usernameChanged || passwordChanged) {
            // Forcé logout pour invalidation session et tokens
            return logout(request, response);
        }
        return ResponseEntity.ok(UserMapper.toDto(updatedUser));
    }

    /**
     * Connecte un utilisateur avec gestion des exceptions d'authentification.
     * @param loginDto données de connexion
     * @param response réponse HTTP avec cookies
     * @return réponse HTTP avec token ou code 401 en erreur
     */
    public ResponseEntity<?> login(LoginRequestDto loginDto, HttpServletResponse response) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            User userEntity = (User) auth.getPrincipal();

            if (userEntity.getDeletedAt() != null) {
                throw new DisabledException("Compte utilisateur supprimé");
            }

            String accessToken = jwtService.generateToken(userEntity);
            String refreshToken = jwtService.generateRefreshToken(userEntity);
            activeTokenService.saveRefreshToken(userEntity.getUsername(), refreshToken);

            writeAccessTokenCookie(response, accessToken);
            Cookie refreshCookie = cookieService.createCookie(REFRESH_TOKEN, refreshToken);
            refreshCookie.setPath("/api/token/refresh");
            response.addCookie(refreshCookie);

            UserResponseDto userResponse = userMapper.toDto(userEntity);
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Connexion réussie");
            body.put("role", userEntity.getRole().name());
            body.put("user", userResponse);

            return ResponseEntity.ok(body);

        } catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Nom d'utilisateur ou mot de passe incorrect"));
        }
    }

    /**
     * Récupère le profil de l'utilisateur connecté par token JWT.
     * @param request requête HTTP
     * @param response réponse HTTP (mise à jour token)
     * @return réponse avec rôle et données, ou token invité si non valide
     */
    public ResponseEntity<?> getUserProfile(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> jwtOpt = extractJwtFromRequest(request);
        if (jwtOpt.isEmpty()) {
            return getGuestResponse(response);
        }

        try {
            String jwt = jwtOpt.get();
            UserDetails userDetails = jwtService.extractUserDetails(jwt);

            if (!jwtService.validateToken(jwt, userDetails)) {
                return getGuestResponse(response);
            }

            User userEntity = (User) userDetails;
            String newToken = jwtService.generateToken(userEntity);
            writeAccessTokenCookie(response, newToken);

            UserResponseDto userResponseDto = userMapper.toDto(userEntity);
            Map<String, Object> body = new HashMap<>();
            body.put("role", userEntity.getRole() != null ? userEntity.getRole().name() : GUEST_ROLE);
            body.put("user", userResponseDto);

            return ResponseEntity.ok(body);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du profil utilisateur", e);
            return getGuestResponse(response);
        }
    }

    /**
     * Déconnecte l'utilisateur et remplace ses cookies par token invité.
     * @param request requête HTTP
     * @param response réponse HTTP
     * @return réponse HTTP 200 OK
     */
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        if (request != null && request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        response.addCookie(cookieService.createCookie(ACCESS_TOKEN, jwtService.generateGuestToken()));
        response.addCookie(cookieService.deleteCookie(REFRESH_TOKEN));
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    /**
     * Supprime un utilisateur donné, supprime tokens/cookies, invalide session.
     * @param id identifiant utilisateur
     * @param request requête HTTP
     * @param response réponse HTTP
     * @return réponse HTTP 204 No Content
     */
    public ResponseEntity<Void> deleteUser(Long id, HttpServletRequest request, HttpServletResponse response) {
        Optional<String> jwtOpt = extractJwtFromRequest(request);
        jwtOpt.ifPresent(jwt -> logger.info("JWT token reçu pour suppression utilisateur"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails ud) {
            logger.info("Utilisateur authentifié pour suppression : {}", ud.getUsername());
        }

        userService.deleteUser(id);

        response.addCookie(cookieService.createCookie(ACCESS_TOKEN, jwtService.generateGuestToken()));
        response.addCookie(cookieService.deleteCookie(REFRESH_TOKEN));

        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        SecurityContextHolder.clearContext();

        return ResponseEntity.noContent().build();
    }
}
