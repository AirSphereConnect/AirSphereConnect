package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.request.ForumThreadRequestDto;
import com.airsphereconnect.dtos.response.ForumThreadResponseDto;
import com.airsphereconnect.entities.ForumRubric;
import com.airsphereconnect.entities.ForumThread;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.entities.enums.UserRole;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.ForumThreadMapper;
import com.airsphereconnect.repositories.ForumRubricRepository;
import com.airsphereconnect.repositories.ForumThreadRepository;
import com.airsphereconnect.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForumThreadServiceImpl Test Suite")
class ForumThreadServiceImplTest {

    @Mock
    private ForumRubricRepository forumRubricRepository;

    @Mock
    private ForumThreadRepository forumThreadRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ForumThreadMapper forumThreadMapper;

    @InjectMocks
    private ForumThreadServiceImpl forumThreadService;

    private User testUser;
    private User testUser2;
    private ForumThread testThread;
    private ForumRubric testRubric;
    private ForumThreadRequestDto testThreadRequestDto;
    private ForumThreadResponseDto testThreadResponseDto;

    @BeforeEach
    void setUp() {

        // creation d'un user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.fr");
        testUser.setRole(UserRole.USER);

        // création d'une autre user
        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setEmail("test2@test.fr");
        testUser2.setRole(UserRole.USER);

        // creation d'une rubrique
        testRubric = new ForumRubric();
        testRubric.setId(1L);
        testRubric.setTitle("test title Rubric");

        // creation d'un thread
        testThread = new ForumThread();
        testThread.setId(1L);
        testThread.setTitle("test title Thread");
        testThread.setUser(testUser);
        testThread.setForumRubric(testRubric);

        // Dto de requêtes
        testThreadRequestDto = new ForumThreadRequestDto();
        testThreadRequestDto.setRubricId(1L);
        testThreadRequestDto.setTitle("test title Thread");

        // DTO de réponses
        testThreadResponseDto = new ForumThreadResponseDto();
        testThreadResponseDto.setId(1L);
        testThreadResponseDto.setTitle("test title Thread");
    }

    @Nested
    @DisplayName("Tests pour createThread")
    class CreateThreadTests {

        @Test
        @DisplayName("Devrait créer un thread avec succès")
        void shouldCreateThreadSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumRubricRepository.findById(1L)).thenReturn(Optional.of(testRubric));
            when(forumThreadRepository.countByUserIdAndDeletedAtIsNull(1L)).thenReturn(0);
            when(forumThreadRepository.countByForumRubricIdAndDeletedAtIsNull(1L)).thenReturn(0);
            when(forumThreadRepository.existsByForumRubricIdAndTitleIgnoreCaseAndDeletedAtIsNull(1L, "test title Thread")).thenReturn(false);
            when(forumThreadMapper.toEntity(testThreadRequestDto, testUser, testRubric)).thenReturn(testThread);
            when(forumThreadRepository.save(testThread)).thenReturn(testThread);
            when(forumThreadMapper.toResponseDto(testThread)).thenReturn(testThreadResponseDto);

            // Act
            ForumThreadResponseDto result = forumThreadService.createThread(testThreadRequestDto, 1L);

            // Assert
            assertNotNull(result);
            assertEquals(testThreadResponseDto.getId(), result.getId());
            assertEquals(testThreadResponseDto.getTitle(), result.getTitle());
            verify(forumThreadRepository).save(testThread);
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'existe pas")
        void shouldThrowsExceptionWhenUserNotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumThreadService.createThread(testThreadRequestDto, 999L)
            );

            assertTrue(exception.getMessage().contains("Utilisateur non trouvé avec l'ID: 999"));
            verify(forumThreadRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si la rubrique n'existe pas")
        void shouldThrowExceptionWhenRubricNotFound() {
            // Arrange
            testThreadRequestDto.setRubricId(999L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumRubricRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumThreadService.createThread(testThreadRequestDto, 1L)
            );

            assertTrue(exception.getMessage().contains("Rubrique non trouvée"));
            verify(forumThreadRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur a atteint la limite de threads")
        void shouldThrowExceptionWhenUserReachedMaxThreads() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumRubricRepository.findById(1L)).thenReturn(Optional.of(testRubric));
            when(forumThreadRepository.countByUserIdAndDeletedAtIsNull(1L)).thenReturn(5);

            GlobalException.BadRequestException exception = assertThrows(
                    GlobalException.BadRequestException.class,
                    () -> forumThreadService.createThread(testThreadRequestDto, 1L)
            );

            assertTrue(exception.getMessage().contains("5 fils de discussion actifs"));
            verify(forumThreadRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si la rubrique a atteint la limite de threads")
        void shouldThrowExceptionWhenRubricReachedMaxThreads() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumRubricRepository.findById(1L)).thenReturn(Optional.of(testRubric));
            when(forumThreadRepository.countByUserIdAndDeletedAtIsNull(1L)).thenReturn(0);
            when(forumThreadRepository.countByForumRubricIdAndDeletedAtIsNull(1L)).thenReturn(10);

            GlobalException.BadRequestException exception = assertThrows(
                    GlobalException.BadRequestException.class,
                    () -> forumThreadService.createThread(testThreadRequestDto, 1L)
            );

            assertTrue(exception.getMessage().contains("limite de 10 fils de discussion actifs"));
            verify(forumThreadRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si un thread avec le même titre existe déjà")
        void shouldThrowExceptionWhenTitleAlreadyExists() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumRubricRepository.findById(1L)).thenReturn(Optional.of(testRubric));
            when(forumThreadRepository.countByUserIdAndDeletedAtIsNull(1L)).thenReturn(0);
            when(forumThreadRepository.countByForumRubricIdAndDeletedAtIsNull(1L)).thenReturn(0);
            when(forumThreadRepository.existsByForumRubricIdAndTitleIgnoreCaseAndDeletedAtIsNull(1L, "test title Thread")).thenReturn(true);

            GlobalException.BadRequestException exception = assertThrows(
                    GlobalException.BadRequestException.class,
                    () -> forumThreadService.createThread(testThreadRequestDto, 1L)
            );

            assertTrue(exception.getMessage().contains("fil de discussion avec ce titre existe déjà"));
            verify(forumThreadRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests pour deleteThread")
    class DeleteThreadTests {

        @Test
        @DisplayName("Devrait supprimer un thread avec succès")
        void shouldDeleteThreadSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumThreadRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testThread));

            // Act
            forumThreadService.deleteThread(1L, 1L);

            // Assert
            verify(forumThreadRepository).save(testThread);
            assertNotNull(testThread.getDeletedAt());
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'est pas l'auteur du thread")
        void shouldThrowExceptionWhenUserIsNotAuthor() {

            // Arrange
            when(userRepository.findById(2L)).thenReturn(Optional.of(testUser2));
            when(forumThreadRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testThread));

            // Act & Assert
            GlobalException.UnauthorizedException exception = assertThrows(
                    GlobalException.UnauthorizedException.class,
                    () -> forumThreadService.deleteThread(1L, 2L)
            );

            assertTrue(exception.getMessage().contains("Vous n'êtes pas autorisé à supprimer ce fil de discussion."));
            verify(forumThreadRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si le thread n'existe pas")
        void shouldThrowExceptionWhenThreadNotFound() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumThreadRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

            // Act & Assert
            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumThreadService.deleteThread(999L, 1L)
            );

            assertTrue(exception.getMessage().contains("Fil de discussion introuvable : " + 999));
        }
    }

    @Nested
    @DisplayName("Tests pour getThreadById")
    class GetThreadByIdTests {

        @Test
        @DisplayName("Devrait récupérer un thread avec succès")
        void shouldGetThreadByIdSuccessfully() {
            // Arrange
            when(forumThreadRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testThread));
            when(forumThreadMapper.toResponseDto(testThread)).thenReturn(testThreadResponseDto);

            // Act
            ForumThreadResponseDto result = forumThreadService.getThreadById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(testThreadResponseDto.getId(), result.getId());
            assertEquals(testThreadResponseDto.getTitle(), result.getTitle());
        }

        @Test
        @DisplayName("Devrait échouer si le thread n'existe pas")
        void shouldThrowExceptionWhenThreadNotFound() {
            // Arrange
            when(forumThreadRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

            // Act & Assert
            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumThreadService.getThreadById(999L)
            );

            assertTrue(exception.getMessage().contains("Fil de discussion introuvable : 999"));
        }
    }

    @Nested
    @DisplayName("Tests pour getAllActiveThreads")
    class GetAllActiveThreadsTests {

        @Test
        @DisplayName("Devrait retourner tous les threads actifs")
        void shouldReturnAllActiveThreads() {
            when(forumThreadRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(testThread));
            when(forumThreadMapper.toResponseDto(testThread)).thenReturn(testThreadResponseDto);

            List<ForumThreadResponseDto> result = forumThreadService.getAllActiveThreads();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testThreadResponseDto.getId(), result.get(0).getId());
        }
    }

    @Nested
    @DisplayName("Tests pour getThreadsByCurrentUser et getThreadsByForumRubricId")
    class GetThreadsTests {

        @Test
        @DisplayName("Devrait retourner les threads de l'utilisateur courant")
        void shouldReturnThreadsByCurrentUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumThreadRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(List.of(testThread));
            when(forumThreadMapper.toResponseDto(testThread)).thenReturn(testThreadResponseDto);

            List<ForumThreadResponseDto> result = forumThreadService.getThreadsByCurrentUser(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Devrait retourner les threads par rubrique")
        void shouldReturnThreadsByForumRubricId() {
            when(forumRubricRepository.findById(1L)).thenReturn(Optional.of(testRubric));
            when(forumThreadRepository.findByForumRubricIdAndDeletedAtIsNull(1L)).thenReturn(List.of(testThread));
            when(forumThreadMapper.toResponseDto(testThread)).thenReturn(testThreadResponseDto);

            List<ForumThreadResponseDto> result = forumThreadService.getThreadsByForumRubricId(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Tests pour updateThread")
    class UpdateThreadTests {

        @Test
        @DisplayName("Devrait mettre à jour un thread avec succès")
        void shouldUpdateThreadSuccessfully() {
            testThreadRequestDto.setTitle("Nouveau titre");
            when(forumThreadRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testThread));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumThreadRepository.save(testThread)).thenReturn(testThread);
            when(forumThreadMapper.toResponseDto(testThread)).thenReturn(testThreadResponseDto);

            ForumThreadResponseDto result = forumThreadService.updateThread(1L, testThreadRequestDto, 1L);

            assertNotNull(result);
            verify(forumThreadRepository).save(testThread);
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'est pas l'auteur")
        void shouldThrowExceptionWhenUserIsNotAuthor() {
            when(forumThreadRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testThread));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testUser2));

            GlobalException.UnauthorizedException exception = assertThrows(
                    GlobalException.UnauthorizedException.class,
                    () -> forumThreadService.updateThread(1L, testThreadRequestDto, 2L)
            );

            assertTrue(exception.getMessage().contains("Vous n'êtes pas autorisé à modifier ce fil de discussion."));
            verify(forumThreadRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests pour countThreadsByRubric et countThreadsByUser")
    class CountTests {

        @Test
        @DisplayName("Devrait retourner le nombre de threads par rubrique")
        void shouldCountThreadsByRubric() {
            when(forumRubricRepository.findById(1L)).thenReturn(Optional.of(testRubric));
            when(forumThreadRepository.countByForumRubricIdAndDeletedAtIsNull(1L)).thenReturn(4);

            int count = forumThreadService.countThreadsByRubric(1L);

            assertEquals(4, count);
        }

        @Test
        @DisplayName("Devrait retourner le nombre de threads par utilisateur")
        void shouldCountThreadsByUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumThreadRepository.countByUserIdAndDeletedAtIsNull(1L)).thenReturn(2);

            int count = forumThreadService.countThreadsByUser(1L);

            assertEquals(2, count);
        }
    }
}
