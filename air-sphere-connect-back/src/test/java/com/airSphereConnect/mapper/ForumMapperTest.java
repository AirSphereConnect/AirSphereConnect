package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.ForumResponseDto;
import com.airSphereConnect.entities.Forum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ForumMapper Test Suite")
class ForumMapperTest {

    private ForumMapper forumMapper;

    @BeforeEach
    void setUp() {
        forumMapper = new ForumMapper();
    }

    @Test
    @DisplayName("toResponseDto should map all fields correctly")
    void toResponseDto_shouldMapAllFields() {
        Forum forum = new Forum();
        forum.setId(1L);
        forum.setTitle("General Discussion");
        forum.setDescription("A place for general topics");

        ForumResponseDto dto = forumMapper.toResponseDto(forum);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("General Discussion");
        assertThat(dto.getDescription()).isEqualTo("A place for general topics");
    }

    @Test
    @DisplayName("toResponseDto should return null for null forum")
    void toResponseDto_shouldReturnNullForNullForum() {
        ForumResponseDto dto = forumMapper.toResponseDto(null);
        assertThat(dto).isNull();
    }
}
