package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.PostReactionRequestDto;
import com.airSphereConnect.dtos.response.PostReactionResponseDto;
import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.PostReaction;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.entities.enums.ReactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostReactionMapper Test Suite")
class PostReactionMapperTest {

    private PostReactionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PostReactionMapper();
    }

    @Nested
    @DisplayName("toEntity tests")
    class ToEntityTests {

        @Test
        @DisplayName("should map request to entity correctly")
        void toEntity_shouldMapCorrectly() {
            PostReactionRequestDto request = new PostReactionRequestDto();
            request.setReaction(ReactionType.LIKE);

            User user = new User();
            user.setId(1L);

            ForumPost post = new ForumPost();
            post.setId(10L);

            PostReaction entity = mapper.toEntity(request, user, post);

            assertThat(entity).isNotNull();
            assertThat(entity.getUser()).isEqualTo(user);
            assertThat(entity.getPost()).isEqualTo(post);
            assertThat(entity.getReactionType()).isEqualTo(ReactionType.LIKE);
        }

        @Test
        @DisplayName("should return null for null request")
        void toEntity_shouldReturnNullForNullRequest() {
            PostReaction entity = mapper.toEntity(null, new User(), new ForumPost());
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

            ForumPost post = new ForumPost();
            post.setId(10L);

            PostReaction reaction = new PostReaction();
            reaction.setId(100L);
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setReactionType(ReactionType.DISLIKE);

            PostReactionResponseDto dto = mapper.toResponseDto(reaction);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(100L);
            assertThat(dto.getUser_id()).isEqualTo(1L);
            assertThat(dto.getPost_id()).isEqualTo(10L);
            assertThat(dto.getReaction()).isEqualTo(ReactionType.DISLIKE);
        }

        @Test
        @DisplayName("should return null for null reaction")
        void toResponseDto_shouldReturnNullForNullReaction() {
            PostReactionResponseDto dto = mapper.toResponseDto(null);
            assertThat(dto).isNull();
        }
    }
}
