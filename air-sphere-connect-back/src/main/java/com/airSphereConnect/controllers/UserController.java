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
 * Contrôleur REST pour la gestion des utilisateurs (CRUD, recherche, contrôle d’accès).
 * Protégé par des autorisations basées sur les rôles (@PreAuthorize).
 */
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
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        boolean usernameTaken = username != null && userService.existsByUsername(username);
        boolean emailTaken = email != null && userService.existsByEmail(email);

        return ResponseEntity.ok(Map.of(
                "usernameTaken", usernameTaken,
                "emailTaken", emailTaken
        ));
    }

    /**
     * Recherche d'utilisateur par nom d’utilisateur (admin uniquement).
     *
     * @param username le nom d'utilisateur à rechercher
     * @return DTO utilisateur trouvé
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/name")
    public UserResponseDto getUserByUsername(@RequestParam String username) {
        return UserMapper.toDto(userService.getUserByUsername(username));
    }

    /**
     * Recherche d'utilisateur par ID (admin uniquement).
     *
     * @param id identifiant utilisateur
     * @return DTO utilisateur trouvé
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/id")
    public UserResponseDto getUserById(@RequestParam Long id) {
        return UserMapper.toDto(userService.getUserById(id));
    }

    /**
     * Création d’un nouvel utilisateur puis connexion automatique après inscription.
     *
     * @param reqDto   données utilisateur reçues
     * @param response réponse HTTP pour gérer les cookies
     * @return réponse avec données utilisateur et tokens
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequestDto reqDto, HttpServletResponse response) {
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
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
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
     * Suppression d’un utilisateur (admin et lui-même).
     *
     * @param id       identifiant utilisateur à supprimer
     * @param request  requête HTTP
     * @param response réponse HTTP pour nettoyage cookies/session
     * @return réponse HTTP sans contenu 204
     */
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestParam Long id, HttpServletRequest request, HttpServletResponse response) {
        return authService.deleteUser(id, request, response);
    }
}
