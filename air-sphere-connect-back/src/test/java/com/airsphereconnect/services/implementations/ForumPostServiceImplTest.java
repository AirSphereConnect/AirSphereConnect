package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.request.ForumPostRequestDto;
import com.airsphereconnect.dtos.response.ForumPostResponseDto;
import com.airsphereconnect.entities.ForumPost;
import com.airsphereconnect.entities.ForumThread;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.entities.enums.UserRole;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.ForumPostMapper;
import com.airsphereconnect.repositories.ForumPostRepository;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForumPostService Tests Suite")
class ForumPostServiceImplTest {

    @Mock
    private ForumPostRepository forumPostRepository;

    @Mock
    private ForumThreadRepository forumThreadRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ForumPostMapper forumPostMapper;

    @InjectMocks
    private ForumPostServiceImpl forumPostService;

    private User testUser;
    private User adminUser;
    private ForumThread testThread;
    private ForumPost testPost;
    private ForumPostRequestDto requestDto;
    private ForumPostResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // Création d'un utilisateur normal
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setRole(UserRole.USER);

        // Création d'un admin
        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(UserRole.ADMIN);

        // Création d'un thread
        testThread = new ForumThread();
        testThread.setId(1L);
        testThread.setTitle("Test Thread");

        // Création d'un post
        testPost = new ForumPost();
        testPost.setId(1L);
        testPost.setContent("Test content");
        testPost.setUser(testUser);
        testPost.setThread(testThread);

        // DTO de requête
        requestDto = new ForumPostRequestDto();
        requestDto.setThreadId(1L);
        requestDto.setContent("Test content");
        requestDto.setUserId(1L);

        // DTO de réponse
        responseDto = new ForumPostResponseDto();
        responseDto.setId(1L);
        responseDto.setContent("Test content");
    }

    @Nested
    @DisplayName("Tests pour createPost")
    class CreatePostTests {

        @Test
        @DisplayName("Devrait créer un post avec succès")
        void shouldCreatePostSuccessfully() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumThreadRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testThread));
            when(forumPostMapper.toEntity(requestDto, testUser, testThread)).thenReturn(testPost);
            when(forumPostRepository.save(testPost)).thenReturn(testPost);
            when(forumPostMapper.toResponseDto(testPost)).thenReturn(responseDto);

            // Act
            ForumPostResponseDto result = forumPostService.createPost(requestDto, 1L);

            // Assert
            assertNotNull(result);
            assertEquals(responseDto.getId(), result.getId());
            assertEquals(responseDto.getContent(), result.getContent());
            verify(userRepository).findById(1L);
            verify(forumThreadRepository).findByIdAndDeletedAtIsNull(1L);
            verify(forumPostRepository).save(testPost);
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'existe pas")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumPostService.createPost(requestDto, 999L)
            );

            assertTrue(exception.getMessage().contains("Utilisateur non trouvé"));
            verify(userRepository).findById(999L);
            verify(forumPostRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si le thread n'existe pas")
        void shouldThrowExceptionWhenThreadNotFound() {
            // Arrange
            requestDto.setThreadId(999L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumThreadRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

            // Act
            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumPostService.createPost(requestDto, 1L)
            );

            // assert
            assertTrue(exception.getMessage().contains("Fil de discussion introuvable"));
            verify(forumPostRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests pour updatePost")
    class UpdatePostTests {

        @Test
        @DisplayName("Devrait mettre à jour un post par l'auteur")
        void shouldUpdatePostByAuthor() {
            // Arrange
            ForumPostRequestDto updateRequest = new ForumPostRequestDto();
            updateRequest.setContent("Updated content");

            when(forumPostRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumPostRepository.save(testPost)).thenReturn(testPost);
            when(forumPostMapper.toResponseDto(testPost)).thenReturn(responseDto);

            // Act
            ForumPostResponseDto result = forumPostService.updatePost(1L, updateRequest, 1L);

            // Assert
            assertNotNull(result);
            assertEquals("Updated content", testPost.getContent());
            verify(forumPostRepository).save(testPost);
        }

        @Test
        @DisplayName("Devrait mettre à jour un post par un admin (pas l'auteur)")
        void shouldUpdatePostByAdmin() {
            // Arrange
            ForumPostRequestDto updateRequest = new ForumPostRequestDto();
            updateRequest.setContent("Updated by admin");

            when(forumPostRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
            when(forumPostRepository.save(testPost)).thenReturn(testPost);
            when(forumPostMapper.toResponseDto(testPost)).thenReturn(responseDto);

            // Act
            ForumPostResponseDto result = forumPostService.updatePost(1L, updateRequest, 2L);

            // Assert
            assertNotNull(result);
            assertEquals("Updated by admin", testPost.getContent());
            verify(forumPostRepository).save(testPost);
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'est ni l'auteur ni admin")
        void shouldThrowExceptionWhenUnauthorizedUserTriesToUpdate() {
            // Arrange
            User otherUser = new User();
            otherUser.setId(3L);
            otherUser.setRole(UserRole.USER);

            ForumPostRequestDto updateRequest = new ForumPostRequestDto();
            updateRequest.setContent("Unauthorized update");

            when(forumPostRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(3L)).thenReturn(Optional.of(otherUser));

            // Act & Assert
            GlobalException.UnauthorizedException exception = assertThrows(
                    GlobalException.UnauthorizedException.class,
                    () -> forumPostService.updatePost(1L, updateRequest, 3L)
            );

            assertTrue(exception.getMessage().contains("Vous n'êtes pas autorisé"));
            verify(forumPostRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si le post n'existe pas")
        void shouldThrowExceptionWhenPostNotFound() {
            // Arrange
            ForumPostRequestDto updateRequest = new ForumPostRequestDto();
            updateRequest.setContent("Update");

            when(forumPostRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

            // Act & Assert
            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumPostService.updatePost(999L, updateRequest, 1L)
            );

            assertTrue(exception.getMessage().contains("Post introuvable"));
        }
    }

    @Nested
    @DisplayName("Tests pour deletePost")
    class DeletePostTests {

        @Test
        @DisplayName("Devrait supprimer un post par l'auteur")
        void shouldDeletePostByAuthor() {
            // Arrange
            when(forumPostRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(forumPostRepository.save(testPost)).thenReturn(testPost);

            // Act
            forumPostService.deletePost(1L, 1L);

            // Assert
            verify(forumPostRepository).save(testPost);
            assertNotNull(testPost.getDeletedAt());
        }

        @Test
        @DisplayName("Devrait supprimer un post par un admin")
        void shouldDeletePostByAdmin() {
            // Arrange
            when(forumPostRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));
            when(forumPostRepository.save(testPost)).thenReturn(testPost);

            // Act
            forumPostService.deletePost(1L, 2L);

            // Assert
            verify(forumPostRepository).save(testPost);
            assertNotNull(testPost.getDeletedAt());
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'est pas autorisé")
        void shouldThrowExceptionWhenUnauthorizedUserTriesToDelete() {
            // Arrange
            User otherUser = new User();
            otherUser.setId(3L);
            otherUser.setRole(UserRole.USER);

            when(forumPostRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(3L)).thenReturn(Optional.of(otherUser));

            // Act & Assert
            GlobalException.UnauthorizedException exception = assertThrows(
                    GlobalException.UnauthorizedException.class,
                    () -> forumPostService.deletePost(1L, 3L)
            );

            assertTrue(exception.getMessage().contains("Vous n'êtes pas autorisé"));
            verify(forumPostRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si le post n'existe pas")
        void shouldThrowExceptionWhenPostToDeleteNotFound() {
            // Arrange
            when(forumPostRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

            // Act & Assert
            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumPostService.deletePost(999L, 1L)
            );

            assertTrue(exception.getMessage().contains("Post introuvable"));
        }
    }

    @Nested
    @DisplayName("Tests pour getPostById")
    class GetPostByIdTests {

        @Test
        @DisplayName("Devrait récupérer un post par son ID")
        void shouldGetPostById() {
            // Arrange
            when(forumPostRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testPost));
            when(forumPostMapper.toResponseDto(testPost)).thenReturn(responseDto);

            // Act
            ForumPostResponseDto result = forumPostService.getPostById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(responseDto.getId(), result.getId());
            verify(forumPostRepository).findByIdWithRelations(1L);
        }

        @Test
        @DisplayName("Devrait échouer si le post n'existe pas")
        void shouldThrowExceptionWhenPostNotFoundById() {
            // Arrange
            when(forumPostRepository.findByIdWithRelations(999L)).thenReturn(Optional.empty());

            // Act & Assert
            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumPostService.getPostById(999L)
            );

            assertTrue(exception.getMessage().contains("Post introuvable"));
        }
    }

    @Nested
    @DisplayName("Tests pour getAllActivePosts")
    class GetAllActivePostsTests {

        @Test
        @DisplayName("Devrait retourner tous les posts actifs")
        void shouldGetAllActivePosts() {
            // Arrange
            ForumPost post2 = new ForumPost();
            post2.setId(2L);
            post2.setContent("Post 2");

            List<ForumPost> posts = Arrays.asList(testPost, post2);
            when(forumPostRepository.findAllWithRelations()).thenReturn(posts);
            when(forumPostMapper.toResponseDto(any(ForumPost.class))).thenReturn(responseDto);

            // Act
            List<ForumPostResponseDto> result = forumPostService.getAllActivePosts();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(forumPostRepository).findAllWithRelations();
        }

        @Test
        @DisplayName("Devrait retourner une liste vide si aucun post")
        void shouldReturnEmptyListWhenNoPosts() {
            // Arrange
            when(forumPostRepository.findAllWithRelations()).thenReturn(List.of());

            // Act
            List<ForumPostResponseDto> result = forumPostService.getAllActivePosts();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Tests pour getPostsByThreadId")
    class GetPostsByThreadIdTests {

        @Test
        @DisplayName("Devrait retourner tous les posts d'un thread")
        void shouldGetPostsByThreadId() {
            // Arrange
            List<ForumPost> posts = Collections.singletonList(testPost);
            when(forumPostRepository.findByThreadIdWithRelations(1L)).thenReturn(posts);
            when(forumPostMapper.toResponseDto(testPost)).thenReturn(responseDto);

            // Act
            List<ForumPostResponseDto> result = forumPostService.getPostsByThreadId(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(forumPostRepository).findByThreadIdWithRelations(1L);
        }

        @Test
        @DisplayName("Devrait retourner une liste vide si le thread n'a pas de posts")
        void shouldReturnEmptyListWhenThreadHasNoPosts() {
            // Arrange
            when(forumPostRepository.findByThreadIdWithRelations(999L)).thenReturn(List.of());

            // Act
            List<ForumPostResponseDto> result = forumPostService.getPostsByThreadId(999L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Tests pour getPostsByUserId")
    class GetPostsByUserIdTests {

        @Test
        @DisplayName("Devrait retourner tous les posts d'un utilisateur")
        void shouldGetPostsByUserId() {
            // Arrange
            List<ForumPost> posts = Collections.singletonList(testPost);
            when(forumPostRepository.findByUserIdWithRelations(1L)).thenReturn(posts);
            when(forumPostMapper.toResponseDto(testPost)).thenReturn(responseDto);

            // Act
            List<ForumPostResponseDto> result = forumPostService.getPostsByUserId(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(forumPostRepository).findByUserIdWithRelations(1L);
        }

        @Test
        @DisplayName("Devrait retourner une liste vide si l'utilisateur n'a pas de posts")
        void shouldReturnEmptyListWhenUserHasNoPosts() {
            // Arrange
            when(forumPostRepository.findByUserIdWithRelations(999L)).thenReturn(List.of());

            // Act
            List<ForumPostResponseDto> result = forumPostService.getPostsByUserId(999L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}