package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.PostReaction;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.entities.enums.ReactionType;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.ForumPostRepository;
import com.airSphereConnect.repositories.PostReactionRepository;
import com.airSphereConnect.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostReactionServiceImpl Test Suite")
class PostReactionServiceImplTest {

    @Mock
    private PostReactionRepository postReactionRepository;
    @Mock
    private ForumPostRepository forumPostRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostReactionServiceImpl postReactionService;

    private User user;
    private ForumPost post;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        post = new ForumPost();
        post.setId(1L);
    }

    @Nested
    @DisplayName("toggleReaction")
    class ToggleReactionTests {

        @Test
        @DisplayName("should create new reaction when none exists")
        void shouldCreateNewReaction() {
            when(forumPostRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(post));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(postReactionRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

            postReactionService.toggleReaction(1L, 1L, ReactionType.LIKE);

            verify(postReactionRepository).save(any(PostReaction.class));
        }

        @Test
        @DisplayName("should soft delete reaction when toggling same type")
        void shouldSoftDeleteWhenSameType() {
            PostReaction existing = new PostReaction(user, post, ReactionType.LIKE);

            when(forumPostRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(post));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(postReactionRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.of(existing));

            postReactionService.toggleReaction(1L, 1L, ReactionType.LIKE);

            verify(postReactionRepository).save(existing);
        }

        @Test
        @DisplayName("should change reaction type when different")
        void shouldChangeReactionType() {
            PostReaction existing = new PostReaction(user, post, ReactionType.LIKE);

            when(forumPostRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(post));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(postReactionRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.of(existing));

            postReactionService.toggleReaction(1L, 1L, ReactionType.DISLIKE);

            assertEquals(ReactionType.DISLIKE, existing.getReactionType());
            verify(postReactionRepository).save(existing);
        }

        @Test
        @DisplayName("should throw when post not found")
        void shouldThrowWhenPostNotFound() {
            when(forumPostRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

            assertThrows(GlobalException.ResourceNotFoundException.class,
                    () -> postReactionService.toggleReaction(99L, 1L, ReactionType.LIKE));
        }

        @Test
        @DisplayName("should throw when user not found")
        void shouldThrowWhenUserNotFound() {
            when(forumPostRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(post));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(GlobalException.ResourceNotFoundException.class,
                    () -> postReactionService.toggleReaction(1L, 99L, ReactionType.LIKE));
        }
    }

    @Nested
    @DisplayName("countReactions")
    class CountReactionsTests {

        @Test
        @DisplayName("should count likes for post")
        void shouldCountLikes() {
            when(postReactionRepository.countByPostIdAndReactionType(1L, ReactionType.LIKE)).thenReturn(5L);

            long result = postReactionService.countLikesByPost(1L);

            assertEquals(5L, result);
        }

        @Test
        @DisplayName("should count dislikes for post")
        void shouldCountDislikes() {
            when(postReactionRepository.countByPostIdAndReactionType(1L, ReactionType.DISLIKE)).thenReturn(2L);

            long result = postReactionService.countDislikesByPost(1L);

            assertEquals(2L, result);
        }
    }

    @Nested
    @DisplayName("getUserReaction")
    class GetUserReactionTests {

        @Test
        @DisplayName("should return user reaction type")
        void shouldReturnReactionType() {
            when(postReactionRepository.findUserReaction(1L, 1L)).thenReturn(ReactionType.LIKE);

            ReactionType result = postReactionService.getUserReaction(1L, 1L);

            assertEquals(ReactionType.LIKE, result);
        }

        @Test
        @DisplayName("should return null when no reaction")
        void shouldReturnNullWhenNoReaction() {
            when(postReactionRepository.findUserReaction(1L, 1L)).thenReturn(null);

            ReactionType result = postReactionService.getUserReaction(1L, 1L);

            assertNull(result);
        }
    }
}
