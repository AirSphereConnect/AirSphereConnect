package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.LoginRequestDto;
import com.airSphereConnect.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST central pour les opérations d'authentification
 * telles que la connexion, la récupération du profil utilisateur et la déconnexion.
 */
@RestController
@RequestMapping("/api")
public class HomeController {

    private final AuthService authService;

    public HomeController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Point d'entrée pour la connexion utilisateur.
     *
     * @param loginDto informations de connexion (username, password)
     * @param response réponse HTTP (cookies JWT seront ajoutés ici)
     * @return responseEntity avec détails de la connexion ou erreurs
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto, HttpServletResponse response) {
        return authService.login(loginDto, response);
    }

    /**
     * Retourne le profil utilisateur connecté en fonction du token JWT reçu.
     *
     * @param request  requête HTTP avec cookie JWT
     * @param response réponse HTTP (cookies mis à jour si nécessaire)
     * @return profil utilisateur ou informations invité
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request, HttpServletResponse response) {
        return authService.getUserProfile(request, response);
    }

    /**
     * Déconnecte l'utilisateur en invalidant la session et supprimant les cookies.
     *
     * @param request  requête HTTP
     * @param response réponse HTTP
     * @return réponse HTTP 200 OK sans contenu
     */
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }
}
