import com.airSphereConnect.entities.User;
import com.airSphereConnect.entities.enums.UserRole;
import com.airSphereConnect.exceptions.GlobalException.BadRequestException;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("jdoe");
        user.setEmail("jdoe@example.com");
        user.setPassword("password");
        user.setRole(UserRole.USER);
    }

    @Test
    void createUser_shouldThrow_whenUsernameExists() {
        when(userRepository.existsByUsernameAndDeletedAtIsNull("jdoe")).thenReturn(true);

        var ex = assertThrows(BadRequestException.class, () -> userService.createUser(user));
        assertEquals("Le nom d'utilisateur existe déjà.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldSaveUser_whenValid() {
        when(userRepository.existsByUsernameAndDeletedAtIsNull("jdoe")).thenReturn(false);
        when(userRepository.existsByEmailAndDeletedAtIsNull("jdoe@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User savedUser = userService.createUser(user);

        assertNotNull(savedUser);
        assertEquals("jdoe", savedUser.getUsername());
        verify(userRepository).save(any());
    }
}

