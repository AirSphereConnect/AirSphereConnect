package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.AuthService;
import com.airSphereConnect.services.UserService;
import com.airSphereConnect.services.security.CookieService;
import com.airSphereConnect.services.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des utilisateurs (CRUD, recherche, contrôle d'accès).
 * Protégé par des autorisations basées sur les rôles (@PreAuthorize).
 */
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs - Inscription, profil, authentification et autorisations")
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final UserRepository userRepository;

    public UserController(UserService userService,
                          AuthService authService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * Récupère tous les utilisateurs (accessible uniquement à l'admin).
     *
     * @return liste des utilisateurs sous forme de DTO
     */
    @Operation(
            summary = "Récupérer tous les utilisateurs (Admin uniquement)",
            description = "Retourne la liste complète des utilisateurs inscrits sur la plateforme"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Réservé aux administrateurs", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    /**
     * Vérifie la disponibilité de noms d'utilisateur et emails.
     *
     * @param username nom utilisateur optionnel à vérifier
     * @param email email optionnel à vérifier
     * @return map indiquant la disponibilité des champs
     */
    @Operation(
            summary = "Vérifier la disponibilité d'un nom d'utilisateur ou email",
            description = "Permet de vérifier si un nom d'utilisateur ou un email est déjà utilisé (utile pour la validation côté frontend)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vérification effectuée avec succès",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @Parameter(description = "Nom d'utilisateur à vérifier", example = "john_doe")
            @RequestParam(required = false) String username,
            @Parameter(description = "Email à vérifier", example = "john@example.com")
            @RequestParam(required = false) String email) {
        boolean usernameTaken = username != null && userService.existsByUsername(username);
        boolean emailTaken = email != null && userService.existsByEmail(email);

        return ResponseEntity.ok(Map.of(
                "usernameTaken", usernameTaken,
                "emailTaken", emailTaken
        ));
    }

    /**
     * Recherche d'utilisateur par nom d'utilisateur (admin uniquement).
     *
     * @param username le nom d'utilisateur à rechercher
     * @return DTO utilisateur trouvé
     */
    @Operation(
            summary = "Rechercher un utilisateur par nom d'utilisateur (Admin uniquement)",
            description = "Retourne les informations d'un utilisateur en utilisant son nom d'utilisateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/name")
    public UserResponseDto getUserByUsername(
            @Parameter(description = "Nom d'utilisateur", example = "john_doe", required = true)
            @RequestParam String username) {
        return UserMapper.toDto(userService.getUserByUsername(username));
    }

    /**
     * Recherche d'utilisateur par ID (admin uniquement).
     *
     * @param id identifiant utilisateur
     * @return DTO utilisateur trouvé
     */
    @Operation(
            summary = "Rechercher un utilisateur par ID (Admin uniquement)",
            description = "Retourne les informations d'un utilisateur en utilisant son identifiant unique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/id")
    public UserResponseDto getUserById(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @RequestParam Long id) {
        return UserMapper.toDto(userService.getUserById(id));
    }

    /**
     * Création d'un nouvel utilisateur puis connexion automatique après inscription.
     *
     * @param reqDto   données utilisateur reçues
     * @param response réponse HTTP pour gérer les cookies
     * @return réponse avec données utilisateur et tokens
     */
    @Operation(
            summary = "Inscription d'un nouvel utilisateur",
            description = "Crée un nouveau compte utilisateur et effectue une connexion automatique. Retourne les tokens JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé et connecté avec succès",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Données invalides (email ou nom d'utilisateur déjà utilisé)", content = @Content)
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du nouvel utilisateur",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRequestDto.class))
            )
            @RequestBody UserRequestDto reqDto,
            HttpServletResponse response) {
        return authService.signupAndLogin(reqDto, response);
    }

    /**
     * Mise à jour des informations utilisateur.
     * L'utilisateur ne peut modifier que son propre profil.
     *
     * @param id          ID utilisateur à modifier
     * @param reqDto      nouvelles données utilisateur
     * @param userDetails utilisateur courant authentifié
     * @param request     requête HTTP
     * @param response    réponse HTTP
     * @return réponses avec le profil mis à jour ou erreur
     */
    @Operation(
            summary = "Mettre à jour le profil utilisateur",
            description = "Permet à un utilisateur de modifier ses propres informations (email, nom, mot de passe, etc.)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil mis à jour avec succès",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Tentative de modification du profil d'un autre utilisateur", content = @Content),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "Identifiant de l'utilisateur à modifier", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations utilisateur",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRequestDto.class))
            )
            @RequestBody UserRequestDto reqDto,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request,
            HttpServletResponse response) {

        User currentUser = userRepository.findByUsernameAndDeletedAtIsNull(userDetails.getUsername())
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé"));

        if (!currentUser.getId().equals(id)) {
            throw new GlobalException.BadRequestException("Vous ne pouvez modifier que votre propre profil.");
        }

        return authService.editUserLogin(reqDto, currentUser, request, response);
    }

    /**
     * Suppression d'un utilisateur (admin et lui-même).
     *
     * @param id       identifiant utilisateur à supprimer
     * @param request  requête HTTP
     * @param response réponse HTTP pour nettoyage cookies/session
     * @return réponse HTTP sans contenu 204
     */
    @Operation(
            summary = "Supprimer un compte utilisateur",
            description = "Permet à un utilisateur de supprimer son propre compte ou à un admin de supprimer n'importe quel compte"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Identifiant de l'utilisateur à supprimer", example = "1", required = true)
            @RequestParam Long id,
            HttpServletRequest request,
            HttpServletResponse response) {
        return authService.deleteUser(id, request, response);
    }
}
