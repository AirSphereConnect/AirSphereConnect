package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.request.PostReportRequestDto;
import com.airsphereconnect.dtos.response.PostReportResponseDto;
import com.airsphereconnect.entities.ForumPost;
import com.airsphereconnect.entities.PostReport;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.entities.enums.ReportReason;
import com.airsphereconnect.entities.enums.UserRole;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.PostReportMapper;
import com.airsphereconnect.repositories.ForumPostRepository;
import com.airsphereconnect.repositories.PostReportRepository;
import com.airsphereconnect.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostReportServiceImpl Test Suite")
class PostReportServiceImplTest {

    @Mock
    private PostReportRepository postReportRepository;

    @Mock
    private ForumPostRepository forumPostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostReportMapper postReportMapper;

    @InjectMocks
    private PostReportServiceImpl postReportService;

    private User reporter;
    private User admin;
    private User postAuthor;
    private ForumPost forumPost;
    private PostReport postReport;
    private PostReportRequestDto requestDto;
    private PostReportResponseDto responseDto;

    @BeforeEach
    void setUp() {
        reporter = new User();
        reporter.setId(1L);
        reporter.setRole(UserRole.USER);

        postAuthor = new User();
        postAuthor.setId(2L);
        postAuthor.setRole(UserRole.USER);

        admin = new User();
        admin.setId(3L);
        admin.setRole(UserRole.ADMIN);

        forumPost = new ForumPost();
        forumPost.setId(10L);
        forumPost.setUser(postAuthor);

        postReport = new PostReport();
        postReport.setId(100L);
        postReport.setUser(reporter);
        postReport.setPost(forumPost);

        requestDto = new PostReportRequestDto(10L, ReportReason.SPAM, "Ce post est du spam");

        responseDto = new PostReportResponseDto(100L, 10L, 1L, ReportReason.SPAM, "Ce post est du spam", "PENDING", LocalDateTime.now());
    }

    @Nested
    @DisplayName("Tests pour createReport")
    class CreateReportTests {

        @Test
        @DisplayName("Devrait créer un signalement avec succès")
        void shouldCreateReportSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));
            when(forumPostRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(forumPost));
            when(postReportRepository.findByPostIdAndUserIdAndDeletedAtIsNull(10L, 1L)).thenReturn(Optional.empty());
            when(postReportMapper.toEntity(requestDto, reporter, forumPost)).thenReturn(postReport);
            when(postReportRepository.save(postReport)).thenReturn(postReport);
            when(postReportMapper.toResponseDto(postReport)).thenReturn(responseDto);

            PostReportResponseDto result = postReportService.createReport(requestDto, 1L);

            assertNotNull(result);
            assertEquals(responseDto.id(), result.id());
            verify(postReportRepository).save(postReport);
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur a déjà signalé ce post")
        void shouldThrowExceptionWhenAlreadyReported() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));
            when(forumPostRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(forumPost));
            when(postReportRepository.findByPostIdAndUserIdAndDeletedAtIsNull(10L, 1L)).thenReturn(Optional.of(postReport));

            GlobalException.UnauthorizedException exception = assertThrows(
                    GlobalException.UnauthorizedException.class,
                    () -> postReportService.createReport(requestDto, 1L)
            );

            assertTrue(exception.getMessage().contains("Vous avez déjà signalé ce post."));
            verify(postReportRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur signale son propre post")
        void shouldThrowExceptionWhenReportingOwnPost() {
            forumPost.setUser(reporter);
            when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));
            when(forumPostRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(forumPost));
            when(postReportRepository.findByPostIdAndUserIdAndDeletedAtIsNull(10L, 1L)).thenReturn(Optional.empty());

            GlobalException.UnauthorizedException exception = assertThrows(
                    GlobalException.UnauthorizedException.class,
                    () -> postReportService.createReport(requestDto, 1L)
            );

            assertTrue(exception.getMessage().contains("Vous ne pouvez pas signaler votre propre post."));
            verify(postReportRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'existe pas")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> postReportService.createReport(requestDto, 999L)
            );
        }

        @Test
        @DisplayName("Devrait échouer si le post n'existe pas")
        void shouldThrowExceptionWhenPostNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));
            when(forumPostRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());

            assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> postReportService.createReport(requestDto, 1L)
            );
        }
    }

    @Nested
    @DisplayName("Tests pour getReportById")
    class GetReportByIdTests {

        @Test
        @DisplayName("Devrait récupérer un signalement avec succès")
        void shouldGetReportByIdSuccessfully() {
            when(postReportRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(postReport));
            when(postReportMapper.toResponseDto(postReport)).thenReturn(responseDto);

            PostReportResponseDto result = postReportService.getReportById(100L);

            assertNotNull(result);
            assertEquals(responseDto.id(), result.id());
        }

        @Test
        @DisplayName("Devrait échouer si le signalement n'existe pas")
        void shouldThrowExceptionWhenReportNotFound() {
            when(postReportRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> postReportService.getReportById(999L)
            );

            assertTrue(exception.getMessage().contains("Signalement introuvable : 999"));
        }
    }

    @Nested
    @DisplayName("Tests pour getReportsByPostId et getReportsByUserId")
    class GetReportsTests {

        @Test
        @DisplayName("Devrait retourner les signalements d'un post")
        void shouldReturnReportsByPostId() {
            when(postReportRepository.findByPostIdAndDeletedAtIsNull(10L)).thenReturn(List.of(postReport));
            when(postReportMapper.toResponseDto(postReport)).thenReturn(responseDto);

            List<PostReportResponseDto> result = postReportService.getReportsByPostId(10L);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Devrait retourner les signalements d'un utilisateur")
        void shouldReturnReportsByUserId() {
            when(postReportRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(List.of(postReport));
            when(postReportMapper.toResponseDto(postReport)).thenReturn(responseDto);

            List<PostReportResponseDto> result = postReportService.getReportsByUserId(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Devrait retourner les signalements par statut")
        void shouldReturnReportsByStatus() {
            when(postReportRepository.findByStatusAndDeletedAtIsNull("PENDING")).thenReturn(List.of(postReport));
            when(postReportMapper.toResponseDto(postReport)).thenReturn(responseDto);

            List<PostReportResponseDto> result = postReportService.getReportsByStatus("PENDING");

            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Tests pour updateReportStatus")
    class UpdateReportStatusTests {

        @Test
        @DisplayName("Devrait mettre à jour le statut avec succès (admin)")
        void shouldUpdateStatusSuccessfully() {
            when(userRepository.findById(3L)).thenReturn(Optional.of(admin));
            when(postReportRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(postReport));
            when(postReportRepository.save(postReport)).thenReturn(postReport);
            when(postReportMapper.toResponseDto(postReport)).thenReturn(responseDto);

            PostReportResponseDto result = postReportService.updateReportStatus(100L, "RESOLVED", 3L);

            assertNotNull(result);
            verify(postReportRepository).save(postReport);
        }

        @Test
        @DisplayName("Devrait échouer si l'utilisateur n'est pas admin")
        void shouldThrowExceptionWhenNotAdmin() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));

            GlobalException.UnauthorizedException exception = assertThrows(
                    GlobalException.UnauthorizedException.class,
                    () -> postReportService.updateReportStatus(100L, "RESOLVED", 1L)
            );

            assertTrue(exception.getMessage().contains("Seuls les administrateurs peuvent mettre à jour"));
        }
    }

    @Nested
    @DisplayName("Tests pour deleteReport")
    class DeleteReportTests {

        @Test
        @DisplayName("Devrait supprimer un signalement (auteur)")
        void shouldDeleteReportAsAuthor() {
            when(postReportRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(postReport));
            when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));

            postReportService.deleteReport(100L, 1L);

            verify(postReportRepository).save(postReport);
            assertNotNull(postReport.getDeletedAt());
        }

        @Test
        @DisplayName("Devrait supprimer un signalement (admin)")
        void shouldDeleteReportAsAdmin() {
            when(postReportRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(postReport));
            when(userRepository.findById(3L)).thenReturn(Optional.of(admin));

            postReportService.deleteReport(100L, 3L);

            verify(postReportRepository).save(postReport);
        }

        @Test
        @DisplayName("Devrait échouer si ni auteur ni admin")
        void shouldThrowExceptionWhenNotAuthorOrAdmin() {
            User otherUser = new User();
            otherUser.setId(5L);
            otherUser.setRole(UserRole.USER);

            when(postReportRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(postReport));
            when(userRepository.findById(5L)).thenReturn(Optional.of(otherUser));

            GlobalException.UnauthorizedException exception = assertThrows(
                    GlobalException.UnauthorizedException.class,
                    () -> postReportService.deleteReport(100L, 5L)
            );

            assertTrue(exception.getMessage().contains("Vous n'êtes pas autorisé à supprimer ce signalement."));
        }
    }

    @Nested
    @DisplayName("Tests pour hasUserReportedPost")
    class HasUserReportedPostTests {

        @Test
        @DisplayName("Devrait retourner true si l'utilisateur a déjà signalé le post")
        void shouldReturnTrueWhenUserAlreadyReported() {
            when(postReportRepository.findByPostIdAndUserIdAndDeletedAtIsNull(10L, 1L)).thenReturn(Optional.of(postReport));

            assertTrue(postReportService.hasUserReportedPost(10L, 1L));
        }

        @Test
        @DisplayName("Devrait retourner false si l'utilisateur n'a pas signalé le post")
        void shouldReturnFalseWhenUserHasNotReported() {
            when(postReportRepository.findByPostIdAndUserIdAndDeletedAtIsNull(10L, 1L)).thenReturn(Optional.empty());

            assertFalse(postReportService.hasUserReportedPost(10L, 1L));
        }
    }
}
