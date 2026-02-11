package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.ForumRubricRequestDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.entities.Forum;
import com.airSphereConnect.entities.ForumRubric;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.repositories.ForumRepository;
import com.airSphereConnect.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForumRubricMapper Test Suite")
class ForumRubricMapperTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ForumRepository forumRepository;

    private ForumRubricMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ForumRubricMapper(userRepository, forumRepository);
    }

    @Nested
    @DisplayName("toEntity tests")
    class ToEntityTests {

        @Test
        @DisplayName("should map request to entity correctly")
        void toEntity_shouldMapCorrectly() {
            ForumRubricRequestDto request = new ForumRubricRequestDto();
            request.setTitle("Tech Rubric");
            request.setDescription("All about technology");

            User user = new User();
            user.setId(1L);

            Forum forum = new Forum();
            forum.setId(1L);

            ForumRubric entity = mapper.toEntity(request, user, forum);

            assertThat(entity).isNotNull();
            assertThat(entity.getTitle()).isEqualTo("Tech Rubric");
            assertThat(entity.getDescription()).isEqualTo("All about technology");
            assertThat(entity.getUser()).isEqualTo(user);
            assertThat(entity.getForum()).isEqualTo(forum);
        }

        @Test
        @DisplayName("should return null for null request")
        void toEntity_shouldReturnNullForNullRequest() {
            ForumRubric entity = mapper.toEntity(null, new User(), new Forum());
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
            forum.setId(2L);
            forum.setTitle("Main Forum");

            ForumRubric rubric = new ForumRubric();
            rubric.setId(10L);
            rubric.setTitle("Tech Topics");
            rubric.setDescription("Discuss tech here");
            rubric.setUser(user);
            rubric.setForum(forum);

            ForumRubricResponseDto dto = mapper.toResponseDto(rubric);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(10L);
            assertThat(dto.getTitle()).isEqualTo("Tech Topics");
            assertThat(dto.getDescription()).isEqualTo("Discuss tech here");
            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getUsername()).isEqualTo("testuser");
            assertThat(dto.getForumId()).isEqualTo(2L);
            assertThat(dto.getForumTitle()).isEqualTo("Main Forum");
        }

        @Test
        @DisplayName("should return null for null rubric")
        void toResponseDto_shouldReturnNullForNullRubric() {
            ForumRubricResponseDto dto = mapper.toResponseDto(null);
            assertThat(dto).isNull();
        }
    }
}
