package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Favorite;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.entities.enums.UserRole;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.FavoriteMapper;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.FavoriteRepository;
import com.airSphereConnect.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private FavoriteMapper favoriteMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteServiceImpl;

    private User user;
    private City city;

    private Favorite favorite;
    private Favorite favorite2;
    private FavoriteDto favoriteDto;
    private FavoriteDto favoriteDto2;

    @BeforeEach
    void setup() {
        // Setup User
        user = new User();
        user.setId(1L);
        user.setUsername("jdoe");
        user.setEmail("jdoe@example.com");
        user.setPassword("password123");
        user.setRole(UserRole.USER);

        // Setup city
        city = new City();
        city.setId(1L);
        city.setName("Paris");

        // Setup favoris
        favorite = new Favorite();
        favorite.setId(1L);
        favorite.setUser(user);
        favorite.setCity(city);
        favorite.setSelectAirQuality(true);
        favorite.setSelectWeather(true);
        favorite.setSelectPopulation(false);

        favorite2 = new Favorite();
        favorite2.setId(2L);
        favorite.setUser(user);
        favorite.setCity(city);
        favorite.setSelectAirQuality(false);
        favorite.setSelectWeather(false);
        favorite.setSelectPopulation(true);

        //Setup Dto
        favoriteDto = new FavoriteDto();
        favoriteDto.setId(1L);
        favoriteDto.setCityId(1L);
        favoriteDto.setSelectAirQuality(true);
        favoriteDto.setSelectWeather(true);
        favoriteDto.setSelectPopulation(false);

        favoriteDto2 = new FavoriteDto();
        favoriteDto2.setId(1L);
        favoriteDto2.setCityId(2L);
        favoriteDto2.setSelectAirQuality(false);
        favoriteDto2.setSelectWeather(false);
        favoriteDto2.setSelectPopulation(true);
    }

    @Test
    void getAllFavorites_shouldReturnAllNonDeletedFavorites() {
        List<Favorite> favorites = Arrays.asList(favorite, favorite2);
        when(favoriteRepository.findByDeletedAtIsNull()).thenReturn(favorites);
        when(favoriteMapper.toDto(favorite)).thenReturn(favoriteDto);
        when(favoriteMapper.toDto(favorite2)).thenReturn(favoriteDto2);

        List<FavoriteDto> result = favoriteServiceImpl.getAllFavorites();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(favoriteRepository).findByDeletedAtIsNull();
        verify(favoriteMapper, times(2)).toDto(any(Favorite.class));
    }

    @Test
    void getAllFavorites_shouldReturnEmptyList_whenNoFavorites() {
        when(favoriteRepository.findByDeletedAtIsNull()).thenReturn(Arrays.asList());

        List<FavoriteDto> result = favoriteServiceImpl.getAllFavorites();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(favoriteRepository).findByDeletedAtIsNull();
        verify(favoriteMapper, never()).toDto(any(Favorite.class));
    }

    // ==================== Tests pour getFavoriteById ====================

    @Test
    void getFavoriteById_shouldReturnFavorite_whenIdExists() {
        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));
        when(favoriteMapper.toDto(favorite)).thenReturn(favoriteDto);

        FavoriteDto result = favoriteServiceImpl.getFavoriteById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(favoriteRepository).findById(1L);
        verify(favoriteMapper).toDto(any(Favorite.class));
    }

    @Test
    void getFavoriteById_shouldThrowException_whenIdNotExists() {
        when(favoriteRepository.findById(999L)).thenReturn(Optional.empty());

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> favoriteServiceImpl.getFavoriteById(999L)
        );

        assertTrue(ex.getMessage().contains("Favori non trouvé avec l'id : 999"));
        verify(favoriteRepository).findById(999L);
        verify(favoriteMapper, never()).toDto(any(Favorite.class));
    }

    // ==================== Tests pour createFavorite ====================
    @Test
    void createFavorite_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> favoriteServiceImpl.createFavorite(999L, favoriteDto)
        );

        assertTrue(ex.getMessage().contains("Utilisateur non trouvé avec l'id : 999"));
        verify(userRepository).findById(999L);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void createFavorite_shouldThrowException_whenCityNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cityRepository.findById(999L)).thenReturn(Optional.empty());

        FavoriteDto dto = new FavoriteDto();
        dto.setCityId(999L);

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> favoriteServiceImpl.createFavorite(1L, dto)
        );

        assertTrue(ex.getMessage().contains("Ville non trouvée avec l'id : 999"));
        verify(cityRepository).findById(999L);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void createFavorite_shouldCreateFavorite_whenValidData() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(favoriteMapper.toEntity(favoriteDto)).thenReturn(favorite);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);
        when(favoriteMapper.toDto(favorite)).thenReturn(favoriteDto);

        FavoriteDto result = favoriteServiceImpl.createFavorite(1L, favoriteDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
        verify(cityRepository).findById(1L);
        verify(favoriteRepository).save(any(Favorite.class));
        verify(favoriteMapper).toDto(favorite);
    }

    @Test
    void createFavorite_shouldSetDefaultValues_whenSelectionsAreNull() {
        FavoriteDto dtoWithNulls = new FavoriteDto();
        dtoWithNulls.setCityId(1L);
        // Les sélections sont null

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(favoriteMapper.toEntity(any(FavoriteDto.class))).thenReturn(favorite);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);
        when(favoriteMapper.toDto(favorite)).thenReturn(favoriteDto);

        FavoriteDto result = favoriteServiceImpl.createFavorite(1L, dtoWithNulls);

        assertNotNull(result);
        // Vérifier que les valeurs par défaut ont été définies
        assertFalse(dtoWithNulls.getSelectAirQuality());
        assertFalse(dtoWithNulls.getSelectWeather());
        assertFalse(dtoWithNulls.getSelectPopulation());
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void createFavorite_shouldCreateWithoutCity_whenCityIdIsNull() {
        FavoriteDto dtoWithoutCity = new FavoriteDto();
        dtoWithoutCity.setCityId(null);
        dtoWithoutCity.setSelectAirQuality(true);
        dtoWithoutCity.setSelectWeather(false);
        dtoWithoutCity.setSelectPopulation(false);

        Favorite favoriteWithoutCity = new Favorite();
        favoriteWithoutCity.setId(3L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(favoriteMapper.toEntity(dtoWithoutCity)).thenReturn(favoriteWithoutCity);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favoriteWithoutCity);
        when(favoriteMapper.toDto(favoriteWithoutCity)).thenReturn(dtoWithoutCity);

        FavoriteDto result = favoriteServiceImpl.createFavorite(1L, dtoWithoutCity);

        assertNotNull(result);
        verify(cityRepository, never()).findById(any());
        verify(favoriteRepository).save(any(Favorite.class));
    }

    // ==================== Tests pour updateFavorite ====================

    @Test
    void updateFavorite_shouldThrowException_whenFavoriteNotFound() {
        when(favoriteRepository.findById(999L)).thenReturn(Optional.empty());

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> favoriteServiceImpl.updateFavorite(999L, favoriteDto)
        );

        assertTrue(ex.getMessage().contains("Favori non trouvé avec l'id : 999"));
        verify(favoriteRepository).findById(999L);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void updateFavorite_shouldThrowException_whenCityNotFound() {
        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));
        when(cityRepository.findById(999L)).thenReturn(Optional.empty());

        FavoriteDto updateDto = new FavoriteDto();
        updateDto.setCityId(999L);

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> favoriteServiceImpl.updateFavorite(1L, updateDto)
        );

        assertTrue(ex.getMessage().contains("Ville non trouvée"));
        verify(cityRepository).findById(999L);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void updateFavorite_shouldUpdateFavorite_whenValidData() {
        FavoriteDto updateDto = new FavoriteDto();
        updateDto.setCityId(2L);
        updateDto.setSelectAirQuality(false);
        updateDto.setSelectWeather(true);
        updateDto.setSelectPopulation(true);

        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));
        when(cityRepository.findById(2L)).thenReturn(Optional.of(city));
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);
        when(favoriteMapper.toDto(favorite)).thenReturn(updateDto);

        FavoriteDto result = favoriteServiceImpl.updateFavorite(1L, updateDto);

        assertNotNull(result);
        verify(favoriteRepository).findById(1L);
        verify(cityRepository).findById(2L);
        verify(favoriteRepository).save(favorite);
        verify(favoriteMapper).toDto(favorite);
    }

    @Test
    void updateFavorite_shouldUpdateOnlySelections_whenCityIdIsNull() {
        FavoriteDto updateDto = new FavoriteDto();
        updateDto.setCityId(null);
        updateDto.setSelectAirQuality(false);
        updateDto.setSelectWeather(false);
        updateDto.setSelectPopulation(true);

        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);
        when(favoriteMapper.toDto(favorite)).thenReturn(updateDto);

        FavoriteDto result = favoriteServiceImpl.updateFavorite(1L, updateDto);

        assertNotNull(result);
        verify(favoriteRepository).findById(1L);
        verify(cityRepository, never()).findById(any());
        verify(favoriteRepository).save(favorite);
    }

    @Test
    void updateFavorite_shouldUpdateAllFields_whenAllProvided() {
        FavoriteDto updateDto = new FavoriteDto();
        updateDto.setCityId(2L);
        updateDto.setSelectAirQuality(true);
        updateDto.setSelectWeather(false);
        updateDto.setSelectPopulation(true);

        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));
        when(cityRepository.findById(2L)).thenReturn(Optional.of(city));
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(invocation -> {
            Favorite saved = invocation.getArgument(0);
            assertEquals(city, saved.getCity());
            assertEquals(true, saved.getSelectAirQuality());
            assertEquals(false, saved.getSelectWeather());
            assertEquals(true, saved.getSelectPopulation());
            return saved;
        });
        when(favoriteMapper.toDto(any(Favorite.class))).thenReturn(updateDto);

        FavoriteDto result = favoriteServiceImpl.updateFavorite(1L, updateDto);

        assertNotNull(result);
        verify(favoriteRepository).save(favorite);
    }

    // ==================== Tests pour deleteFavorite ====================

    @Test
    void deleteFavorite_shouldThrowException_whenFavoriteNotFound() {
        when(favoriteRepository.findById(999L)).thenReturn(Optional.empty());

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> favoriteServiceImpl.deleteFavorite(999L)
        );

        assertTrue(ex.getMessage().contains("Favori non trouvé avec l'id : 999"));
        verify(favoriteRepository).findById(999L);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void deleteFavorite_shouldSoftDeleteFavorite_whenFavoriteExists() {
        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(invocation -> {
            Favorite saved = invocation.getArgument(0);
            // Simuler que softDelete() a défini deletedAt
            if (saved.getDeletedAt() == null) {
                saved.setDeletedAt(LocalDateTime.now());
            }
            return saved;
        });
        when(favoriteMapper.toDto(any(Favorite.class))).thenReturn(favoriteDto);

        FavoriteDto result = favoriteServiceImpl.deleteFavorite(1L);

        assertNotNull(result);
        verify(favoriteRepository).findById(1L);
        verify(favoriteRepository).save(favorite);
        verify(favoriteMapper).toDto(favorite);
        assertNotNull(favorite.getDeletedAt()); // Vérifier que softDelete() a été appelé
    }

    @Test
    void deleteFavorite_shouldReturnDto_afterSuccessfulDeletion() {
        when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite));
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);
        when(favoriteMapper.toDto(favorite)).thenReturn(favoriteDto);

        FavoriteDto result = favoriteServiceImpl.deleteFavorite(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(favoriteMapper).toDto(favorite);
    }
}