package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.request.ForumRubricRequestDto;
import com.airsphereconnect.dtos.response.ForumRubricResponseDto;
import com.airsphereconnect.entities.Forum;
import com.airsphereconnect.entities.ForumRubric;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.entities.enums.UserRole;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.ForumRubricMapper;
import com.airsphereconnect.repositories.ForumRepository;
import com.airsphereconnect.repositories.ForumRubricRepository;
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
@DisplayName("ForumRubricServiceImpl Test Suite")
class ForumRubricServiceImplTest {

    @Mock
    private ForumRubricRepository forumRubricRepository;

    @Mock
    private ForumRepository forumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ForumRubricMapper forumRubricMapper;

    @InjectMocks
    private ForumRubricServiceImpl forumRubricService;

    private User testUser;
    private User testAdmin;
    private Forum forum;
    private ForumRubric forumRubric;
    private ForumRubricRequestDto requestDto;
    private ForumRubricResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // création d'un user
        testUser = new User();
        testUser.setId(1L);
        testUser.setRole(UserRole.USER);

        // création d'un admin
        testAdmin = new User();
        testAdmin.setId(2L);
        testAdmin.setRole(UserRole.ADMIN);

        // création d'un forum
        forum = new Forum();
        forum.setId(1L);
        forum.setTitle("Forum Title");

        // création d'une rubrique
        forumRubric = new ForumRubric();
        forumRubric.setTitle("Forum Rubric Title");
        forumRubric.setId(1L);
        forumRubric.setUser(testAdmin);
        forumRubric.setForum(forum);

        // création d'une request Dto
        requestDto = new ForumRubricRequestDto();
        requestDto.setForumId(1L);
        requestDto.setTitle("Forum Title");
        requestDto.setUserId(1L);

        // création d'une response Dto
        responseDto = new ForumRubricResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Forum Rubric Title");

    }

    @Nested
    @DisplayName("Test pour createRubric")
    class CreateRubricTests {

        @Test
        @DisplayName("Devrait créer une rubrique avec succès")
        void shouldCreateRubricSuccessfully() {
            // Arrange
            when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
            when(forumRepository.findById(1L)).thenReturn(Optional.of(forum));
            when(forumRubricRepository.countByUserIdAndDeletedAtIsNull(2L)).thenReturn(0);
            when(forumRubricRepository.countByForumIdAndDeletedAtIsNull(1L)).thenReturn(0);
            when(forumRubricRepository.existsByForumIdAndTitleIgnoreCaseAndDeletedAtIsNull(1L, "Forum Title")).thenReturn(false);
            when(forumRubricMapper.toEntity(requestDto, testAdmin, forum)).thenReturn(forumRubric);
            when(forumRubricRepository.save(forumRubric)).thenReturn(forumRubric);
            when(forumRubricMapper.toResponseDto(forumRubric)).thenReturn(responseDto);

            // Act
            ForumRubricResponseDto result = forumRubricService.createRubric(requestDto, 2L);

            // Assert
            assertNotNull(result);
            assertEquals(responseDto.getId(), result.getId());
            assertEquals(responseDto.getTitle(), result.getTitle());
            verify(userRepository).findById(2L);
            verify(forumRepository).findById(1L);
            verify(forumRubricRepository).save(forumRubric);
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'existe pas")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumRubricService.createRubric(requestDto, 999L)
            );

            assertTrue(exception.getMessage().contains("Utilisateur non trouvé avec l'ID: 999"));
            verify(forumRubricRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si le forum n'existe pas")
        void shouldThrowExceptionWhenForumNotFound() {
            requestDto.setForumId(999L);
            when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
            when(forumRepository.findById(999L)).thenReturn(Optional.empty());

            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumRubricService.createRubric(requestDto, 2L)
            );

            assertTrue(exception.getMessage().contains("Forum non trouvé avec l'ID: 999"));
            verify(forumRubricRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur a atteint la limite de rubriques")
        void shouldThrowExceptionWhenUserReachedMaxRubrics() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
            when(forumRepository.findById(1L)).thenReturn(Optional.of(forum));
            when(forumRubricRepository.countByUserIdAndDeletedAtIsNull(2L)).thenReturn(5);

            GlobalException.BadRequestException exception = assertThrows(
                    GlobalException.BadRequestException.class,
                    () -> forumRubricService.createRubric(requestDto, 2L)
            );

            assertTrue(exception.getMessage().contains("5 rubriques actives"));
            verify(forumRubricRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si le forum a atteint la limite de rubriques")
        void shouldThrowExceptionWhenForumReachedMaxRubrics() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
            when(forumRepository.findById(1L)).thenReturn(Optional.of(forum));
            when(forumRubricRepository.countByUserIdAndDeletedAtIsNull(2L)).thenReturn(0);
            when(forumRubricRepository.countByForumIdAndDeletedAtIsNull(1L)).thenReturn(20);

            GlobalException.BadRequestException exception = assertThrows(
                    GlobalException.BadRequestException.class,
                    () -> forumRubricService.createRubric(requestDto, 2L)
            );

            assertTrue(exception.getMessage().contains("limite de 20 rubriques actives"));
            verify(forumRubricRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si une rubrique avec le même titre existe déjà")
        void shouldThrowExceptionWhenTitleAlreadyExists() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
            when(forumRepository.findById(1L)).thenReturn(Optional.of(forum));
            when(forumRubricRepository.countByUserIdAndDeletedAtIsNull(2L)).thenReturn(0);
            when(forumRubricRepository.countByForumIdAndDeletedAtIsNull(1L)).thenReturn(0);
            when(forumRubricRepository.existsByForumIdAndTitleIgnoreCaseAndDeletedAtIsNull(1L, "Forum Title")).thenReturn(true);

            GlobalException.BadRequestException exception = assertThrows(
                    GlobalException.BadRequestException.class,
                    () -> forumRubricService.createRubric(requestDto, 2L)
            );

            assertTrue(exception.getMessage().contains("rubrique avec ce titre existe déjà"));
            verify(forumRubricRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests pour getRubricById")
    class GetRubricByIdTests {

        @Test
        @DisplayName("Devrait récupérer une rubrique avec succès")
        void shouldGetRubricByIdSuccessfully() {
            when(forumRubricRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(forumRubric));
            when(forumRubricMapper.toResponseDto(forumRubric)).thenReturn(responseDto);

            ForumRubricResponseDto result = forumRubricService.getRubricById(1L);

            assertNotNull(result);
            assertEquals(responseDto.getId(), result.getId());
        }

        @Test
        @DisplayName("Devrait échouer si la rubrique n'existe pas")
        void shouldThrowExceptionWhenRubricNotFound() {
            when(forumRubricRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumRubricService.getRubricById(999L)
            );

            assertTrue(exception.getMessage().contains("Rubrique introuvable : 999"));
        }
    }

    @Nested
    @DisplayName("Tests pour getAllActiveRubrics")
    class GetAllActiveRubricsTests {

        @Test
        @DisplayName("Devrait retourner toutes les rubriques actives")
        void shouldReturnAllActiveRubrics() {
            when(forumRubricRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(forumRubric));
            when(forumRubricMapper.toResponseDto(forumRubric)).thenReturn(responseDto);

            List<ForumRubricResponseDto> result = forumRubricService.getAllActiveRubrics();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(responseDto.getId(), result.get(0).getId());
        }
    }

    @Nested
    @DisplayName("Tests pour updateRubric")
    class UpdateRubricTests {

        @Test
        @DisplayName("Devrait mettre à jour une rubrique avec succès")
        void shouldUpdateRubricSuccessfully() {
            requestDto.setTitle("Nouveau titre");
            requestDto.setDescription("Nouvelle description");

            when(forumRubricRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(forumRubric));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
            when(forumRubricRepository.save(forumRubric)).thenReturn(forumRubric);
            when(forumRubricMapper.toResponseDto(forumRubric)).thenReturn(responseDto);

            ForumRubricResponseDto result = forumRubricService.updateRubric(1L, requestDto, 2L);

            assertNotNull(result);
            verify(forumRubricRepository).save(forumRubric);
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'est pas l'auteur")
        void shouldThrowExceptionWhenUserIsNotAuthor() {
            when(forumRubricRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(forumRubric));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            GlobalException.UnauthorizedException exception = assertThrows(
                    GlobalException.UnauthorizedException.class,
                    () -> forumRubricService.updateRubric(1L, requestDto, 1L)
            );

            assertTrue(exception.getMessage().contains("Vous n'êtes pas autorisé à modifier cette rubrique."));
            verify(forumRubricRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests pour deleteRubric")
    class DeleteRubricTests {

        @Test
        @DisplayName("Devrait supprimer une rubrique avec succès")
        void shouldDeleteRubricSuccessfully() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
            when(forumRubricRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(forumRubric));

            forumRubricService.deleteRubric(1L, 2L);

            verify(forumRubricRepository).save(forumRubric);
            assertNotNull(forumRubric.getDeletedAt());
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'est pas l'auteur")
        void shouldThrowExceptionWhenUserIsNotAuthor() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumRubricRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(forumRubric));

            GlobalException.UnauthorizedException exception = assertThrows(
                    GlobalException.UnauthorizedException.class,
                    () -> forumRubricService.deleteRubric(1L, 1L)
            );

            assertTrue(exception.getMessage().contains("Vous n'êtes pas autorisé à supprimer cette rubrique."));
            verify(forumRubricRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests pour countRubricsByUser et countRubricsByForum")
    class CountTests {

        @Test
        @DisplayName("Devrait retourner le nombre de rubriques par utilisateur")
        void shouldCountRubricsByUser() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
            when(forumRubricRepository.countByUserIdAndDeletedAtIsNull(2L)).thenReturn(3);

            int count = forumRubricService.countRubricsByUser(2L);

            assertEquals(3, count);
        }

        @Test
        @DisplayName("Devrait retourner le nombre de rubriques par forum")
        void shouldCountRubricsByForum() {
            when(forumRepository.findById(1L)).thenReturn(Optional.of(forum));
            when(forumRubricRepository.countByForumIdAndDeletedAtIsNull(1L)).thenReturn(7);

            int count = forumRubricService.countRubricsByForum(1L);

            assertEquals(7, count);
        }
    }

    @Nested
    @DisplayName("Tests pour getRubricsByCurrentUser et getRubricsByForumId")
    class GetRubricsTests {

        @Test
        @DisplayName("Devrait retourner les rubriques de l'utilisateur courant")
        void shouldReturnRubricsByCurrentUser() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
            when(forumRubricRepository.findByUserIdAndDeletedAtIsNull(2L)).thenReturn(List.of(forumRubric));
            when(forumRubricMapper.toResponseDto(forumRubric)).thenReturn(responseDto);

            List<ForumRubricResponseDto> result = forumRubricService.getRubricsByCurrentUser(2L);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Devrait retourner les rubriques par forum")
        void shouldReturnRubricsByForumId() {
            when(forumRepository.findById(1L)).thenReturn(Optional.of(forum));
            when(forumRubricRepository.findByForumIdAndDeletedAtIsNull(1L)).thenReturn(List.of(forumRubric));
            when(forumRubricMapper.toResponseDto(forumRubric)).thenReturn(responseDto);

            List<ForumRubricResponseDto> result = forumRubricService.getRubricsByForumId(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Tests pour findByForumIdOrdered et findByUserIdOrdered")
    class FindOrderedTests {

        @Test
        @DisplayName("Devrait retourner les rubriques d'un forum par ordre ASC")
        void shouldFindByForumIdOrderByCreatedAtAsc() {
            when(forumRubricRepository.findByForumIdAndDeletedAtIsNullOrderByCreatedAtAsc(1L)).thenReturn(List.of(forumRubric));

            List<ForumRubric> result = forumRubricService.findByForumIdOrderByCreatedAtAsc(1L);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Devrait retourner les rubriques d'un forum par ordre DESC")
        void shouldFindByForumIdOrderByCreatedAtDesc() {
            when(forumRubricRepository.findByForumIdAndDeletedAtIsNullOrderByCreatedAtDesc(1L)).thenReturn(List.of(forumRubric));

            List<ForumRubric> result = forumRubricService.findByForumIdOrderByCreatedAtDesc(1L);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Devrait retourner les rubriques d'un utilisateur par ordre ASC")
        void shouldFindByUserIdOrderByCreatedAtAsc() {
            when(forumRubricRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtAsc(2L)).thenReturn(List.of(forumRubric));

            List<ForumRubric> result = forumRubricService.findByUserIdOrderByCreatedAtAsc(2L);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Devrait retourner les rubriques d'un utilisateur par ordre DESC")
        void shouldFindByUserIdOrderByCreatedAtDesc() {
            when(forumRubricRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(2L)).thenReturn(List.of(forumRubric));

            List<ForumRubric> result = forumRubricService.findByUserIdOrderByCreatedAtDesc(2L);

            assertEquals(1, result.size());
        }
    }
}
