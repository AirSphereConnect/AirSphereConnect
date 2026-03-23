package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.request.ForumThreadRequestDto;
import com.airsphereconnect.dtos.response.ForumThreadResponseDto;
import com.airsphereconnect.entities.Forum;
import com.airsphereconnect.entities.ForumRubric;
import com.airsphereconnect.entities.ForumThread;
import com.airsphereconnect.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ForumThreadMapper Test Suite")
class ForumThreadMapperTest {

    private ForumThreadMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ForumThreadMapper();
    }

    @Nested
    @DisplayName("toEntity tests")
    class ToEntityTests {

        @Test
        @DisplayName("should map request to entity correctly")
        void toEntity_shouldMapCorrectly() {
            ForumThreadRequestDto request = new ForumThreadRequestDto();
            request.setTitle("New Thread");

            User user = new User();
            user.setId(1L);

            ForumRubric rubric = new ForumRubric();
            rubric.setId(1L);

            ForumThread entity = mapper.toEntity(request, user, rubric);

            assertThat(entity).isNotNull();
            assertThat(entity.getTitle()).isEqualTo("New Thread");
            assertThat(entity.getUser()).isEqualTo(user);
            assertThat(entity.getForumRubric()).isEqualTo(rubric);
        }

        @Test
        @DisplayName("should return null for null request")
        void toEntity_shouldReturnNullForNullRequest() {
            ForumThread entity = mapper.toEntity(null, new User(), new ForumRubric());
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

            Forum forum = new Forum();
            forum.setId(1L);

            ForumRubric rubric = new ForumRubric();
            rubric.setId(2L);
            rubric.setTitle("Tech Topics");
            rubric.setForum(forum);

            ForumThread thread = new ForumThread();
            thread.setId(10L);
            thread.setTitle("Test Thread");
            thread.setUser(user);
            thread.setForumRubric(rubric);

            ForumThreadResponseDto dto = mapper.toResponseDto(thread);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(10L);
            assertThat(dto.getTitle()).isEqualTo("Test Thread");
            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getUsername()).isEqualTo("testuser");
            assertThat(dto.getRubricId()).isEqualTo(2L);
            assertThat(dto.getRubricTitle()).isEqualTo("Tech Topics");
        }

        @Test
        @DisplayName("should return null for null thread")
        void toResponseDto_shouldReturnNullForNullThread() {
            ForumThreadResponseDto dto = mapper.toResponseDto(null);
            assertThat(dto).isNull();
        }
    }
}
