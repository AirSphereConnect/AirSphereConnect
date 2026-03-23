package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.response.UserResponseDto;
import com.airsphereconnect.entities.Address;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.entities.enums.UserRole;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.UserMapper;
import com.airsphereconnect.repositories.UserRepository;
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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User user2;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("jdoe");
        user.setEmail("jdoe@example.com");
        user.setPassword("password123");
        user.setRole(UserRole.USER);

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("asmith");
        user2.setEmail("asmith@example.com");
        user2.setPassword("password456");
        user2.setRole(UserRole.USER);
    }

    // ==================== Tests pour getAllUsers ====================

    @Test
    void getAllUsers_shouldReturnAllNonDeletedUsers() {
        List<User> users = Arrays.asList(user, user2);
        when(userRepository.findByDeletedAtIsNull()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("jdoe", result.get(0).getUsername());
        assertEquals("asmith", result.get(1).getUsername());
        verify(userRepository).findByDeletedAtIsNull();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findByDeletedAtIsNull()).thenReturn(Arrays.asList());

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findByDeletedAtIsNull();
    }

    // ==================== Tests pour getUserByUsername ====================

    @Test
    void getUserByUsername_shouldReturnUser_whenUsernameExists() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("jdoe"))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("jdoe");

        assertNotNull(result);
        assertEquals("jdoe", result.getUsername());
        assertEquals("jdoe@example.com", result.getEmail());
        verify(userRepository).findByUsernameAndDeletedAtIsNull("jdoe");
    }

    @Test
    void getUserByUsername_shouldThrowException_whenUsernameNotExists() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("unknown"))
                .thenReturn(Optional.empty());

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> userService.getUserByUsername("unknown")
        );

        assertTrue(ex.getMessage().contains("Utilisateur non trouvé avec le username : unknown"));
        verify(userRepository).findByUsernameAndDeletedAtIsNull("unknown");
    }

    // ==================== Tests pour getUserById ====================

    @Test
    void getUserById_shouldReturnUser_whenIdExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("jdoe", result.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_shouldThrowException_whenIdNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> userService.getUserById(999L)
        );

        assertTrue(ex.getMessage().contains("Utilisateur non trouvé avec l'id : 999"));
        verify(userRepository).findById(999L);
    }

    // ==================== Tests pour createUser ====================

    @Test
    void createUser_shouldThrowException_whenUsernameAlreadyExists() {
        when(userRepository.existsByUsernameAndDeletedAtIsNull("jdoe")).thenReturn(true);

        GlobalException.BadRequestException ex = assertThrows(
                GlobalException.BadRequestException.class,
                () -> userService.createUser(user)
        );

        assertEquals("Le nom d'utilisateur existe déjà.", ex.getMessage());
        verify(userRepository).existsByUsernameAndDeletedAtIsNull("jdoe");
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByUsernameAndDeletedAtIsNull("jdoe")).thenReturn(false);
        when(userRepository.existsByEmailAndDeletedAtIsNull("jdoe@example.com")).thenReturn(true);

        GlobalException.BadRequestException ex = assertThrows(
                GlobalException.BadRequestException.class,
                () -> userService.createUser(user)
        );

        assertEquals("L'email existe déjà.", ex.getMessage());
        verify(userRepository).existsByEmailAndDeletedAtIsNull("jdoe@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldSaveUser_whenValidData() {
        when(userRepository.existsByUsernameAndDeletedAtIsNull("jdoe")).thenReturn(false);
        when(userRepository.existsByEmailAndDeletedAtIsNull("jdoe@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("jdoe", result.getUsername());
        assertEquals("jdoe@example.com", result.getEmail());
        assertNotEquals("password123", result.getPassword()); // Password should be encoded
        assertTrue(result.getPassword().startsWith("$2a$")); // BCrypt format
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldSetUserInAddress_whenAddressProvided() {
        Address address = new Address();
        address.setStreet("123 Main St");
        user.setAddress(address);

        when(userRepository.existsByUsernameAndDeletedAtIsNull("jdoe")).thenReturn(false);
        when(userRepository.existsByEmailAndDeletedAtIsNull("jdoe@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(user);

        assertNotNull(result.getAddress());
        assertEquals(result, result.getAddress().getUser());
        verify(userRepository).save(any(User.class));
    }

    // ==================== Tests pour updateUser ====================

    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> userService.updateUser(999L, user)
        );

        assertTrue(ex.getMessage().contains("Utilisateur non trouvé avec l'id : 999"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowException_whenNewUsernameAlreadyExists() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldusername");
        existingUser.setEmail("old@example.com");

        User updateData = new User();
        updateData.setUsername("jdoe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsernameAndDeletedAtIsNull("jdoe")).thenReturn(Optional.of(user2));

        GlobalException.BadRequestException ex = assertThrows(
                GlobalException.BadRequestException.class,
                () -> userService.updateUser(1L, updateData)
        );

        assertEquals("Le nom d'utilisateur existe déjà.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowException_whenNewEmailAlreadyExists() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("jdoe");
        existingUser.setEmail("old@example.com");

        User updateData = new User();
        updateData.setEmail("asmith@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmailAndDeletedAtIsNull("asmith@example.com")).thenReturn(Optional.of(user2));

        GlobalException.BadRequestException ex = assertThrows(
                GlobalException.BadRequestException.class,
                () -> userService.updateUser(1L, updateData)
        );

        assertEquals("L'email existe déjà.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldUpdateUsername_whenValid() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldusername");
        existingUser.setEmail("jdoe@example.com");

        User updateData = new User();
        updateData.setUsername("newusername");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsernameAndDeletedAtIsNull("newusername")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, updateData);

        assertEquals("newusername", result.getUsername());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_shouldUpdateEmail_whenValid() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("jdoe");
        existingUser.setEmail("old@example.com");

        User updateData = new User();
        updateData.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmailAndDeletedAtIsNull("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, updateData);

        assertEquals("new@example.com", result.getEmail());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_shouldEncodePassword_whenPasswordProvided() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("jdoe");
        existingUser.setEmail("jdoe@example.com");
        existingUser.setPassword("oldpassword");

        User updateData = new User();
        updateData.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, updateData);

        assertNotEquals("newpassword", result.getPassword());
        assertTrue(result.getPassword().startsWith("$2a$"));
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_shouldNotUpdatePassword_whenPasswordEmpty() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("jdoe");
        existingUser.setEmail("jdoe@example.com");
        existingUser.setPassword("oldpassword");

        User updateData = new User();
        updateData.setPassword("");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, updateData);

        assertEquals("oldpassword", result.getPassword());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_shouldAllowSameUsername_forSameUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("jdoe");
        existingUser.setEmail("jdoe@example.com");

        User updateData = new User();
        updateData.setUsername("jdoe");
        updateData.setEmail("newemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmailAndDeletedAtIsNull("newemail@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, updateData);

        assertEquals("jdoe", result.getUsername());
        assertEquals("newemail@example.com", result.getEmail());
        verify(userRepository).save(existingUser);
    }

    // ==================== Tests pour deleteUser ====================

    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        GlobalException.ResourceNotFoundException ex = assertThrows(
                GlobalException.ResourceNotFoundException.class,
                () -> userService.deleteUser(999L)
        );

        assertTrue(ex.getMessage().contains("Utilisateur non trouvé avec l'id : 999"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_shouldSoftDeleteUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            // Simuler que la méthode softDelete() a défini deletedAt
            if (savedUser.getDeletedAt() == null) {
                // Forcer la définition pour le test
                savedUser.setDeletedAt(LocalDateTime.now());
            }
            return savedUser;
        });

        UserResponseDto mockResponse = new UserResponseDto();
        mockResponse.setId(1L);
        mockResponse.setUsername("jdoe");

        // Mock du mapper statique (si nécessaire)
        // Note: Pour un vrai test, vous devriez peut-être injecter UserMapper comme dépendance

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        assertNotNull(user.getDeletedAt()); // Vérifier que softDelete() a été appelé
    }

    // ==================== Tests pour findByUsername ====================

    @Test
    void findByUsername_shouldReturnOptionalOfUser_whenUsernameExists() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("jdoe"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("jdoe");

        assertTrue(result.isPresent());
        assertEquals("jdoe", result.get().getUsername());
        verify(userRepository).findByUsernameAndDeletedAtIsNull("jdoe");
    }

    @Test
    void findByUsername_shouldReturnEmptyOptional_whenUsernameNotExists() {
        when(userRepository.findByUsernameAndDeletedAtIsNull("unknown"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("unknown");

        assertFalse(result.isPresent());

         verify(userRepository).findByUsernameAndDeletedAtIsNull("unknown");
    }

    // ==================== Tests pour existsByUsername ====================

    @Test
    void existsByUsername_shouldReturnTrue_whenUsernameExists() {
        when(userRepository.existsByUsernameAndDeletedAtIsNull("jdoe")).thenReturn(true);

        boolean result = userService.existsByUsername("jdoe");

        assertTrue(result);
        verify(userRepository).existsByUsernameAndDeletedAtIsNull("jdoe");
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUsernameNotExists() {
        when(userRepository.existsByUsernameAndDeletedAtIsNull("unknown")).thenReturn(false);

        boolean result = userService.existsByUsername("unknown");

        assertFalse(result);
        verify(userRepository).existsByUsernameAndDeletedAtIsNull("unknown");
    }

    // ==================== Tests pour existsByEmail ====================

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        when(userRepository.existsByEmailAndDeletedAtIsNull("jdoe@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("jdoe@example.com");

        assertTrue(result);
        verify(userRepository).existsByEmailAndDeletedAtIsNull("jdoe@example.com");
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailNotExists() {
        when(userRepository.existsByEmailAndDeletedAtIsNull("unknown@example.com")).thenReturn(false);

        boolean result = userService.existsByEmail("unknown@example.com");

        assertFalse(result);
        verify(userRepository).existsByEmailAndDeletedAtIsNull("unknown@example.com");
    }
}