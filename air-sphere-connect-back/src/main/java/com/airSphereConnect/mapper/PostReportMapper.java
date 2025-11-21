package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.PostReportRequestDto;
import com.airSphereConnect.dtos.response.PostReportResponseDto;
import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.PostReport;
import com.airSphereConnect.entities.User;
import org.springframework.stereotype.Component;

@Component
public class PostReportMapper {

    public PostReport toEntity(PostReportRequestDto dto, User user, ForumPost post) {
        PostReport report = new PostReport(user, post, dto.reason());
        report.setDescription(dto.description());
        return report;
    }

    public PostReportResponseDto toResponseDto(PostReport entity) {
        return new PostReportResponseDto(
                entity.getId(),
                entity.getPost().getId(),
                entity.getUser().getId(),
                entity.getReason(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}