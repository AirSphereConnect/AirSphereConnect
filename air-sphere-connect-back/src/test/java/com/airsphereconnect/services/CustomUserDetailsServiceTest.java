package com.airsphereconnect.services;

import com.airsphereconnect.entities.User;
import com.airsphereconnect.entities.enums.UserRole;
import com.airsphereconnect.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Test Suite")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.fr");
        testUser.setRole(UserRole.USER);
    }

    @Nested
    @DisplayName("Tests pour loadUserByUsername")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Devrait charger l'utilisateur avec succès")
        void shouldLoadUserSuccessfully() {
            when(userRepository.findByUsernameAndDeletedAtIsNull("testuser"))
                    .thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'existe pas")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findByUsernameAndDeletedAtIsNull("inconnu"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UsernameNotFoundException.class,
                    () -> customUserDetailsService.loadUserByUsername("inconnu")
            );
        }
    }
}
