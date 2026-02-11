package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.PostReportRequestDto;
import com.airSphereConnect.dtos.response.PostReportResponseDto;
import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.PostReport;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.entities.enums.ReportReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostReportMapper Test Suite")
class PostReportMapperTest {

    private PostReportMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PostReportMapper();
    }

    @Nested
    @DisplayName("toEntity tests")
    class ToEntityTests {

        @Test
        @DisplayName("should map request to entity correctly")
        void toEntity_shouldMapCorrectly() {
            PostReportRequestDto request = new PostReportRequestDto(10L, ReportReason.SPAM, "Spam content");

            User user = new User();
            user.setId(1L);

            ForumPost post = new ForumPost();
            post.setId(10L);

            PostReport entity = mapper.toEntity(request, user, post);

            assertThat(entity).isNotNull();
            assertThat(entity.getUser()).isEqualTo(user);
            assertThat(entity.getPost()).isEqualTo(post);
            assertThat(entity.getReason()).isEqualTo(ReportReason.SPAM);
            assertThat(entity.getDescription()).isEqualTo("Spam content");
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

            PostReport report = new PostReport(user, post, ReportReason.HARASSMENT);
            report.setId(100L);
            report.setDescription("Offensive language");
            report.setStatus("PENDING");

            PostReportResponseDto dto = mapper.toResponseDto(report);

            assertThat(dto).isNotNull();
            assertThat(dto.id()).isEqualTo(100L);
            assertThat(dto.postId()).isEqualTo(10L);
            assertThat(dto.userId()).isEqualTo(1L);
            assertThat(dto.reason()).isEqualTo(ReportReason.HARASSMENT);
            assertThat(dto.description()).isEqualTo("Offensive language");
            assertThat(dto.status()).isEqualTo("PENDING");
        }
    }
}
