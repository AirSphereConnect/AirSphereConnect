package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Composant Spring de mapping entre l'entité User et ses différents DTOs.
 * Cette classe assure la conversion bidirectionnelle entre les objets utilisés dans
 * la couche métier (entités) et ceux adaptés pour les échanges avec le frontend (DTOs).
 *
 * La conversion des objets associés (adresse, favoris, alertes) est déléguée aux mappers spécialisés.
 */
@Component
public class UserMapper {

    private static FavoriteMapper favoriteMapper;
    private static FavoritesAlertsMapper favoritesAlertsMapper;
    private static AddressMapper addressMapper;

    /**
     * Injection statique des mappers dépendants via constructeur.
     * Permet d’utiliser ces mappers pour convertir les sous-objets.
     *
     * @param favoriteMapper        mapper des favoris utilisateurs
     * @param favoritesAlertsMapper mapper des alertes de favoris
     * @param addressMapper         mapper des adresses utilisateurs
     */
    public UserMapper(FavoriteMapper favoriteMapper, FavoritesAlertsMapper favoritesAlertsMapper, AddressMapper addressMapper) {
        UserMapper.favoriteMapper = favoriteMapper;
        UserMapper.favoritesAlertsMapper = favoritesAlertsMapper;
        UserMapper.addressMapper = addressMapper;
    }

    /**
     * Convertit un DTO {@link UserRequestDto} en entité {@link User}.
     * Utile pour les traitements backend suivant réception d’un objet DTO.
     *
     * @param request DTO utilisateur en entrée
     * @return entité User construite, ou null si l'entrée est nulle
     */
    public static User toEntity(UserRequestDto request) {
        if (request == null) return null;

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        // Conversion de l'adresse via AddressMapper
        user.setAddress(addressMapper.toEntity(request.getAddress(), user));

        return user;
    }

    /**
     * Convertit une entité {@link User} en DTO {@link UserResponseDto} à destination du frontend.
     * Ne retourne pas d’objet si l’utilisateur est marqué supprimé (deletedAt non nul).
     * Inclut la conversion des entités liées (adresse, favoris, alertes) via leurs mappers respectifs.
     *
     * @param user entité User source
     * @return DTO UserResponseDto ou null si utilisateur supprimé ou null
     */
    public static UserResponseDto toDto(User user) {
        if (user == null || user.getDeletedAt() != null) return null;

        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setDeletedAt(user.getDeletedAt());

        // Conversion de l'adresse associée
        response.setAddress(addressMapper.toDto(user.getAddress()));

        // Conversion des favoris actifs (non supprimés)
        if (user.getFavorites() != null) {
            List<FavoriteDto> favoriteDtos = user.getFavorites().stream()
                    .filter(fav -> fav.getDeletedAt() == null)
                    .map(favoriteMapper::toDto)
                    .collect(Collectors.toList());
            response.setFavorites(favoriteDtos);
        }

        // Conversion des alertes favoris actifs (non supprimés)
        if (user.getFavoritesAlerts() != null) {
            List<FavoritesAlertsDto> favoritesAlertsDtos = user.getFavoritesAlerts().stream()
                    .filter(fav -> fav.getDeletedAt() == null)
                    .map(favoritesAlertsMapper::toDto)
                    .collect(Collectors.toList());
            response.setAlerts(favoritesAlertsDtos);
        }

        return response;
    }
}
