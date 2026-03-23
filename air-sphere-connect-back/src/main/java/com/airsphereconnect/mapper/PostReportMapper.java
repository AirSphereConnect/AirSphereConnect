package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.request.PostReportRequestDto;
import com.airsphereconnect.dtos.response.PostReportResponseDto;
import com.airsphereconnect.entities.ForumPost;
import com.airsphereconnect.entities.PostReport;
import com.airsphereconnect.entities.User;
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