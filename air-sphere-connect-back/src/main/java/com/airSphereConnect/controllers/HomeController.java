package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.LoginRequestDto;
import com.airSphereConnect.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST central pour les opérations d'authentification
 * telles que la connexion, la récupération du profil utilisateur et la déconnexion.
 */
@Tag(name = "Authentification", description = "API d'authentification - Connexion, profil utilisateur, déconnexion")
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
    @Operation(
            summary = "Connexion utilisateur",
            description = "Authentifie un utilisateur avec son nom d'utilisateur et mot de passe. Retourne un token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations de connexion",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequestDto.class))
            )
            @RequestBody LoginRequestDto loginDto,
            HttpServletResponse response) {
        return authService.login(loginDto, response);
    }

    /**
     * Retourne le profil utilisateur connecté en fonction du token JWT reçu.
     *
     * @param request  requête HTTP avec cookie JWT
     * @param response réponse HTTP (cookies mis à jour si nécessaire)
     * @return profil utilisateur ou informations invité
     */
    @Operation(
            summary = "Récupérer le profil utilisateur connecté",
            description = "Retourne les informations du profil de l'utilisateur actuellement connecté basé sur le token JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil utilisateur récupéré",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
    })
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
    @Operation(
            summary = "Déconnexion utilisateur",
            description = "Déconnecte l'utilisateur en invalidant sa session et en supprimant les cookies JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie", content = @Content)
    })
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }
}
