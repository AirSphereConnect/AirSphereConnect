package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.FavoritesAlerts;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.entities.enums.UserRole;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.FavoritesAlertsMapper;
import com.airSphereConnect.repositories.FavoritesAlertsRepository;
import com.airSphereConnect.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritesAlertsServiceImplTest {

    @Mock
    private FavoritesAlertsMapper favoritesAlertsMapper;

    @Mock
    private FavoritesAlertsRepository favoritesAlertsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FavoritesAlertsServiceImpl favoritesAlertsService;

    private User user;
    private City city;
    private FavoritesAlerts favoriteAlerts;
    private FavoritesAlerts favoriteAlerts2;
    private FavoritesAlertsDto favoritesAlertsDto;
    private FavoritesAlertsDto favoritesAlertsDto2;

    @BeforeEach
    void setup() {
        // Setup User
        user = new User();
        user.setId(1L);
        user.setUsername("jdoe");
        user.setEmail("jdoe@example.com");
        user.setRole(UserRole.USER);

        // Setup City
        city = new City();
        city.setId(1L);
        city.setName("Paris");

        // Setup FavoritesAlerts 1
        favoriteAlerts = new FavoritesAlerts();
        favoriteAlerts.setId(1L);
        favoriteAlerts.setUser(user);
        favoriteAlerts.setCity(city);
        favoriteAlerts.setEnabled(true);

        // Setup FavoritesAlerts 2
        favoriteAlerts2 = new FavoritesAlerts();
        favoriteAlerts2.setId(2L);
        favoriteAlerts2.setUser(user);
        favoriteAlerts2.setCity(city);
        favoriteAlerts2.setEnabled(false);

        // Setup FavoritesAlertsDto 1
        favoritesAlertsDto = new FavoritesAlertsDto();
        favoritesAlertsDto.setId(1L);
        favoritesAlertsDto.setUser(1L);
        favoritesAlertsDto.setCityId(1L);
        favoritesAlertsDto.setEnabled(true);

        // Setup FavoritesAlertsDto 2
        favoritesAlertsDto2 = new FavoritesAlertsDto();
        favoritesAlertsDto2.setId(2L);
        favoritesAlertsDto2.setUser(1L);
        favoritesAlertsDto2.setCityId(1L);
        favoritesAlertsDto2.setEnabled(false);
    }

    // ==================== Tests pour getAllFavoritesAlerts ====================

    @Test
    @DisplayName("Devrait retourner toutes les alertes favorites")
    void getAllFavoritesAlerts_shouldReturnAllAlerts() {
        List<FavoritesAlerts> alerts = Arrays.asList(favoriteAlerts, favoriteAlerts2);
        when(favoritesAlertsRepository.findAll()).thenReturn(alerts);
        when(favoritesAlertsMapper.toDto(favoriteAlerts)).thenReturn(favoritesAlertsDto);
        when(favoritesAlertsMapper.toDto(favoriteAlerts2)).thenReturn(favoritesAlertsDto2);

        List<FavoritesAlertsDto> result = favoritesAlertsService.getAllFavoritesAlerts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(favoritesAlertsRepository).findAll();
        verify(favoritesAlertsMapper, times(2)).toDto(any(FavoritesAlerts.class));
    }

    @Test
    @DisplayName("Devrait retourner une liste vide quand aucune alerte")
    void getAllFavoritesAlerts_shouldReturnEmptyList_whenNoAlerts() {
        when(favoritesAlertsRepository.findAll()).thenReturn(Arrays.asList());

        List<FavoritesAlertsDto> result = favoritesAlertsService.getAllFavoritesAlerts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(favoritesAlertsRepository).findAll();
        verify(favoritesAlertsMapper, never()).toDto(any());
    }

    // ==================== Tests pour getUserAlerts ====================

    // PROBLÈME DANS VOTRE CODE: getUserAlerts utilise findById au lieu de findByUserId
    // Voici les tests basés sur l'implémentation actuelle (incorrecte)

    @Test
    @DisplayName("Devrait retourner les alertes de l'utilisateur quand l'ID existe")
    void getUserAlerts_shouldReturnUserAlerts_whenUserIdExists() {
        // ATTENTION: Votre implémentation actuelle utilise findById(userId)
        // Ce qui est probablement incorrect - devrait être findByUserId(userId)
        when(favoritesAlertsRepository.findById(1L)).thenReturn(Optional.of(favoriteAlerts));
        when(favoritesAlertsMapper.toDto(favoriteAlerts)).thenReturn(favoritesAlertsDto);

        List<FavoritesAlertsDto> result = favoritesAlertsService.getUserAlerts(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(favoritesAlertsRepository).findById(1L);
        verify(favoritesAlertsMapper).toDto(favoriteAlerts);
    }

    @Test
    @DisplayName("Devrait retourner une liste vide quand l'utilisateur n'a pas d'alertes")
    void getUserAlerts_shouldReturnEmptyList_whenUserHasNoAlerts() {
        when(favoritesAlertsRepository.findById(999L)).thenReturn(Optional.empty());

        List<FavoritesAlertsDto> result = favoritesAlertsService.getUserAlerts(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(favoritesAlertsRepository).findById(999L);
        verify(favoritesAlertsMapper, never()).toDto(any());
    }

    // ==================== Tests pour createAlertConfig ====================

    @Test
    @DisplayName("Devrait lancer une exception quand l'utilisateur n'existe pas")
    void createAlertConfig_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> favoritesAlertsService.createAlertConfig(999L, favoritesAlertsDto)
        );

        assertTrue(ex.getMessage().contains("Utilisateur non trouvé avec l'id : 999"));
        verify(userRepository).findById(999L);
        verify(favoritesAlertsRepository, never()).save(any());
    }

    @Test
    @DisplayName("Devrait créer une alerte avec enabled=false par défaut quand enabled est null")
    void createAlertConfig_shouldSetEnabledToFalse_whenEnabledIsNull() {
        FavoritesAlertsDto dtoWithNullEnabled = new FavoritesAlertsDto();
        dtoWithNullEnabled.setCityId(1L);
        dtoWithNullEnabled.setEnabled(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(favoritesAlertsRepository.save(any(FavoritesAlerts.class))).thenReturn(favoriteAlerts);
        when(favoritesAlertsMapper.toDto(favoriteAlerts)).thenReturn(favoritesAlertsDto);

        FavoritesAlertsDto result = favoritesAlertsService.createAlertConfig(1L, dtoWithNullEnabled);

        assertNotNull(result);
        assertFalse(dtoWithNullEnabled.getEnabled()); // Vérifie que enabled a été mis à false
        verify(favoritesAlertsRepository).save(any(FavoritesAlerts.class));
    }

    @Test
    @DisplayName("Devrait créer une configuration d'alerte avec succès")
    void createAlertConfig_shouldCreateAlertConfig_whenValidData() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(favoritesAlertsRepository.save(any(FavoritesAlerts.class))).thenReturn(favoriteAlerts);
        when(favoritesAlertsMapper.toDto(favoriteAlerts)).thenReturn(favoritesAlertsDto);

        FavoritesAlertsDto result = favoritesAlertsService.createAlertConfig(1L, favoritesAlertsDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
        verify(favoritesAlertsRepository).save(any(FavoritesAlerts.class));
        verify(favoritesAlertsMapper).toDto(favoriteAlerts);
    }

    // ==================== Tests pour updateAlertConfig ====================

    @Test
    @DisplayName("Devrait lancer une exception quand l'alerte n'existe pas ou n'appartient pas à l'utilisateur")
    void updateAlertConfig_shouldThrowException_whenAlertNotFoundOrNotOwned() {
        when(favoritesAlertsRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> favoritesAlertsService.updateAlertConfig(favoritesAlertsDto, 1L, 999L)
        );

        assertTrue(ex.getMessage().contains("Alert config not found or not owned by this user"));
        verify(favoritesAlertsRepository).findByIdAndUserId(999L, 1L);
        verify(favoritesAlertsRepository, never()).save(any());
    }

    @Test
    @DisplayName("Devrait mettre à jour la configuration d'alerte avec succès")
    void updateAlertConfig_shouldUpdateAlertConfig_whenValidData() {
        FavoritesAlertsDto updateDto = new FavoritesAlertsDto();
        updateDto.setUser(1L);
        updateDto.setCityId(1L);
        updateDto.setEnabled(true);

        when(favoritesAlertsRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(favoriteAlerts));
        when(favoritesAlertsRepository.save(any(FavoritesAlerts.class))).thenReturn(favoriteAlerts);
        when(favoritesAlertsMapper.toDto(favoriteAlerts)).thenReturn(favoritesAlertsDto);

        FavoritesAlertsDto result = favoritesAlertsService.updateAlertConfig(updateDto, 1L, 1L);

        assertNotNull(result);
        verify(favoritesAlertsRepository).findByIdAndUserId(1L, 1L);
        verify(favoritesAlertsRepository).save(favoriteAlerts);
        verify(favoritesAlertsMapper).toDto(favoriteAlerts);
    }

    @Test
    @DisplayName("Devrait mettre enabled à false quand null est passé")
    void updateAlertConfig_shouldSetEnabledToFalse_whenNullPassed() {
        FavoritesAlertsDto updateDto = new FavoritesAlertsDto();
        updateDto.setEnabled(null);

        when(favoritesAlertsRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(favoriteAlerts));
        when(favoritesAlertsRepository.save(any(FavoritesAlerts.class))).thenAnswer(invocation -> {
            FavoritesAlerts saved = invocation.getArgument(0);
            assertFalse(saved.isEnabled()); // Vérifie que enabled est false
            return saved;
        });
        when(favoritesAlertsMapper.toDto(any())).thenReturn(favoritesAlertsDto);

        favoritesAlertsService.updateAlertConfig(updateDto, 1L, 1L);

        verify(favoritesAlertsRepository).save(favoriteAlerts);
    }

    @Test
    @DisplayName("Devrait mettre à jour uniquement enabled sans changer user et city")
    void updateAlertConfig_shouldUpdateOnlyEnabled_whenUserAndCityAreNull() {
        FavoritesAlertsDto updateDto = new FavoritesAlertsDto();
        updateDto.setUser(null);
        updateDto.setCityId(null);
        updateDto.setEnabled(false);

        User originalUser = favoriteAlerts.getUser();
        City originalCity = favoriteAlerts.getCity();

        when(favoritesAlertsRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(favoriteAlerts));
        when(favoritesAlertsRepository.save(any(FavoritesAlerts.class))).thenAnswer(invocation -> {
            FavoritesAlerts saved = invocation.getArgument(0);
            assertEquals(originalUser, saved.getUser());
            assertEquals(originalCity, saved.getCity());
            assertFalse(saved.isEnabled());
            return saved;
        });
        when(favoritesAlertsMapper.toDto(any())).thenReturn(favoritesAlertsDto);

        favoritesAlertsService.updateAlertConfig(updateDto, 1L, 1L);

        verify(favoritesAlertsRepository).save(favoriteAlerts);
    }

    // ==================== Tests pour deleteAlertConfig ====================

    @Test
    @DisplayName("Devrait supprimer la configuration d'alerte")
    void deleteAlertConfig_shouldDeleteAlertConfig() {
        doNothing().when(favoritesAlertsRepository).deleteById(1L);

        favoritesAlertsService.deleteAlertConfig(1L);

        verify(favoritesAlertsRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Devrait appeler deleteById même si l'alerte n'existe pas")
    void deleteAlertConfig_shouldCallDeleteById_evenIfAlertDoesNotExist() {
        doNothing().when(favoritesAlertsRepository).deleteById(999L);

        // Ne lance pas d'exception dans l'implémentation actuelle
        assertDoesNotThrow(() -> favoritesAlertsService.deleteAlertConfig(999L));

        verify(favoritesAlertsRepository).deleteById(999L);
    }
}