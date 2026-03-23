package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.request.ForumPostRequestDto;
import com.airsphereconnect.dtos.response.ForumPostResponseDto;
import com.airsphereconnect.entities.ForumPost;
import com.airsphereconnect.entities.ForumThread;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.entities.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ForumPostMapper Test Suite")
class ForumPostMapperTest {

    private ForumPostMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ForumPostMapper();
    }

    @Nested
    @DisplayName("toEntity tests")
    class ToEntityTests {

        @Test
        @DisplayName("should map request to entity correctly")
        void toEntity_shouldMapCorrectly() {
            ForumPostRequestDto request = new ForumPostRequestDto();
            request.setContent("Test content");

            User user = new User();
            user.setId(1L);

            ForumThread thread = new ForumThread();
            thread.setId(1L);

            ForumPost entity = mapper.toEntity(request, user, thread);

            assertThat(entity).isNotNull();
            assertThat(entity.getContent()).isEqualTo("Test content");
            assertThat(entity.getUser()).isEqualTo(user);
            assertThat(entity.getThread()).isEqualTo(thread);
        }

        @Test
        @DisplayName("should return null for null request")
        void toEntity_shouldReturnNullForNullRequest() {
            ForumPost entity = mapper.toEntity(null, new User(), new ForumThread());
            assertThat(entity).isNull();
        }
    }

    @Nested
    @DisplayName("toResponseDto tests")
    class ToResponseDtoTests {

        @Test
        @DisplayName("should map entity to response DTO correctly")
        void toResponseDto_shouldMapCorrectly() {
            User user = new User();
            user.setId(1L);
            user.setUsername("testuser");
            user.setRole(UserRole.USER);

            ForumThread thread = new ForumThread();
            thread.setId(10L);
            thread.setTitle("Test Thread");

            ForumPost post = new ForumPost();
            post.setId(100L);
            post.setContent("Post content");
            post.setUser(user);
            post.setThread(thread);

            ForumPostResponseDto dto = mapper.toResponseDto(post);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(100L);
            assertThat(dto.getContent()).isEqualTo("Post content");
            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getUsername()).isEqualTo("testuser");
            assertThat(dto.getThreadId()).isEqualTo(10L);
            assertThat(dto.getThreadTitle()).isEqualTo("Test Thread");
            assertThat(dto.getLikeCount()).isZero();
            assertThat(dto.getDislikeCount()).isZero();
            assertThat(dto.getCurrentUserReaction()).isNull();
        }

        @Test
        @DisplayName("should return null for null post")
        void toResponseDto_shouldReturnNullForNullPost() {
            ForumPostResponseDto dto = mapper.toResponseDto(null);
            assertThat(dto).isNull();
        }
    }
}
